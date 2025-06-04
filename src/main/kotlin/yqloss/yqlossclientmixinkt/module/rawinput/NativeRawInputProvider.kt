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

package yqloss.yqlossclientmixinkt.module.rawinput

import org.lwjgl.input.Mouse
import org.lwjgl.opengl.Display
import yqloss.yqlossclientmixinkt.nativeapi.cancelClipCursor
import yqloss.yqlossclientmixinkt.nativeapi.registerRawInputDevices
import yqloss.yqlossclientmixinkt.nativeapi.unregisterRawInputDevices
import yqloss.yqlossclientmixinkt.util.accessor.getValue
import yqloss.yqlossclientmixinkt.util.accessor.refs.trigger
import yqloss.yqlossclientmixinkt.util.scope.noThrow

object NativeRawInputProvider : RawInputProvider {
    val lockClipCursor = Any()

    var rawInputMode = false
        private set

    val enabledAndGrabbed get() = RawInput.options.enabled && RawInput.provider === this && Mouse.isGrabbed()

    private val onGrabStateChange: Unit by trigger(::enabledAndGrabbed) { ver ->
        noThrow {
            val grabbed = Mouse.isGrabbed()
            Mouse.setGrabbed(!grabbed)
            Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2)
            Mouse.setGrabbed(grabbed)
            Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2)
            if (ver) {
                RawInput.logger.info("registering raw input")
                registerRawInputDevices()
                synchronized(lockClipCursor) {
                    rawInputMode = true
                }
            } else {
                RawInput.logger.info("unregistering raw input")
                unregisterRawInputDevices()
                synchronized(lockClipCursor) {
                    rawInputMode = false
                    cancelClipCursor()
                }
            }
        }
        Unit
    }

    override fun update() {
        onGrabStateChange
    }

    fun handleMouseMove(
        x: Double,
        y: Double,
    ) {
        if (!RawInput.options.enabled || RawInput.provider !== this) return

        RawInput.x += x
        RawInput.y += y
    }
}
