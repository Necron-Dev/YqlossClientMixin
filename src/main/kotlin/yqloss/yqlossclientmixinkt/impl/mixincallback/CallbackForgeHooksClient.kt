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

package yqloss.yqlossclientmixinkt.impl.mixincallback

import cc.polyfrost.oneconfig.renderer.NanoVGHelper
import net.minecraft.client.gui.GuiScreen
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import yqloss.yqlossclientmixinkt.YC
import yqloss.yqlossclientmixinkt.event.minecraft.YCRenderEvent
import yqloss.yqlossclientmixinkt.impl.nanovgui.GUIEvent
import yqloss.yqlossclientmixinkt.impl.nanovgui.NanoVGUIContext
import yqloss.yqlossclientmixinkt.impl.oneconfiginternal.loadFonts
import yqloss.yqlossclientmixinkt.impl.oneconfiginternal.nvg
import yqloss.yqlossclientmixinkt.util.glStateScope

object CallbackForgeHooksClient {
    object YqlossClient {
        private var drawYCScreenAtPost = false

        private fun drawYCScreen() {
            GUIEvent.Screen
                .Post()
                .also(YC.eventDispatcher)
                .apply {
                    glStateScope {
                        val helper = NanoVGHelper.INSTANCE
                        helper.setupAndDraw { vg ->
                            nvg.loadFonts(vg)
                            val context = NanoVGUIContext(helper, vg)
                            helper.setAlpha(vg, 1.0F)
                            widgets.forEach { it.draw(context) }
                        }
                    }
                }
        }

        fun drawScreenPre(
            screen: GuiScreen,
            mouseX: Int,
            mouseY: Int,
            partialTicks: Float,
            ci: CallbackInfo,
        ) {
            YCRenderEvent.Screen
                .Proxy(screen)
                .also(YC.eventDispatcher)
                .mutableScreen
                ?.run {
                    ci.cancel()
                    drawScreen(mouseX, mouseY, partialTicks)
                    drawYCScreen()
                }
                ?: run {
                    drawYCScreenAtPost = true
                }
        }

        fun drawScreenPost() {
            if (drawYCScreenAtPost) {
                drawYCScreen()
            }
        }
    }
}
