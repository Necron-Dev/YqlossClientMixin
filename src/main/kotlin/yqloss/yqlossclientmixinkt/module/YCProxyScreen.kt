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

package yqloss.yqlossclientmixinkt.module

import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import yqloss.yqlossclientmixinkt.YC
import yqloss.yqlossclientmixinkt.event.minecraft.YCInputEvent
import yqloss.yqlossclientmixinkt.util.MC
import yqloss.yqlossclientmixinkt.util.accessor.getValue
import yqloss.yqlossclientmixinkt.util.accessor.refs.trigger

abstract class YCProxyScreen<T : GuiScreen> : GuiScreen() {
    var proxiedScreen: T? = null

    private var screenVersion: Any = Unit

    var onHandleInput: (() -> Unit)? = null

    private val setupScreen by trigger(::screenVersion) {
        val sr = ScaledResolution(MC)
        mc = MC
        itemRender = MC.renderItem
        fontRendererObj = MC.fontRendererObj
        width = sr.scaledWidth
        height = sr.scaledHeight
    }

    fun setScreen(screen: T) {
        proxiedScreen = screen
        screenVersion = screen to
            ScaledResolution(MC).run {
                scaledWidth to scaledHeight to scaleFactor
            }
        setupScreen
    }

    override fun mouseClicked(
        mouseX: Int,
        mouseY: Int,
        mouseButton: Int,
    ) {
        YC.eventDispatcher(YCInputEvent.Mouse.Click(true, mouseButton))
    }

    override fun keyTyped(
        typedChar: Char,
        keyCode: Int,
    ) {
        val proxiedScreen = proxiedScreen
        if (proxiedScreen !== null && (keyCode == 1 || keyCode == MC.gameSettings.keyBindInventory.keyCode)) {
            YC.api.call_GuiScreen_keyTyped(proxiedScreen, typedChar, keyCode)
        } else {
            super.keyTyped(typedChar, keyCode)
        }
    }

    override fun handleInput() {
        onHandleInput.also { onHandleInput = null }?.invoke()
    }
}
