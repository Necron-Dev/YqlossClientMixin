/*
 * Copyright (C) 2025 Yqloss
 *
 * This file is part of Yqloss Client (Mixin).
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 (GPLv2)
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Yqloss Client (Mixin). If not, see <https://www.gnu.org/licenses/old-licenses/gpl-2.0.html>.
 */

package yqloss.yqlossclientmixinkt.impl.nanovgui

import cc.polyfrost.oneconfig.renderer.NanoVGHelper
import net.minecraft.client.renderer.GlStateManager
import net.yqloss.uktil.event.Event
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL14.glBlendFuncSeparate
import yqloss.yqlossclientmixinkt.impl.oneconfiginternal.loadFonts
import yqloss.yqlossclientmixinkt.impl.oneconfiginternal.nvg
import yqloss.yqlossclientmixinkt.util.glStateScope

sealed interface GUIEvent : Event {
    val widgets: MutableList<Widget<*>>

    fun render() {
        glStateScope {
            widgets.forEach { it.preDraw() }
            val helper = NanoVGHelper.INSTANCE
            helper.setupAndDraw { vg ->
                nvg.loadFonts(vg)
                val context = NanoVGUIContext(helper, vg)
                helper.setAlpha(vg, 1.0F)
                widgets.forEach { it.draw(context) }
            }
            widgets.forEach { it.postDraw() }
        }
        GlStateManager.enableTexture2D()
        glEnable(GL_TEXTURE_2D)
        GlStateManager.enableBlend()
        glEnable(GL_BLEND)
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO)
        glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO)
        GlStateManager.disableAlpha()
        glDisable(GL_ALPHA_TEST)
        GlStateManager.depthMask(true)
        glDepthMask(true)
        GlStateManager.enableDepth()
        glEnable(GL_DEPTH_TEST)
        GlStateManager.depthFunc(GL_LEQUAL)
        glDepthFunc(GL_LEQUAL)
        GlStateManager.disableLighting()
        glDisable(GL_LIGHTING)
    }

    sealed interface HUD : GUIEvent {
        data class Post(
            override val widgets: MutableList<Widget<*>> = mutableListOf(),
        ) : HUD
    }

    sealed interface Screen : GUIEvent {
        data class Post(
            override val widgets: MutableList<Widget<*>> = mutableListOf(),
        ) : Screen
    }
}
