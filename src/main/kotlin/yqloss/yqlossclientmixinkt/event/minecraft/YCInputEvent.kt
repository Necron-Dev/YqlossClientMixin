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

package yqloss.yqlossclientmixinkt.event.minecraft

import net.yqloss.uktil.event.Event
import net.yqloss.uktil.math.Vec2I
import yqloss.yqlossclientmixinkt.util.MC
import org.lwjgl.input.Mouse as lwjglMouse

sealed interface YCInputEvent : Event {
    sealed interface Mouse : YCInputEvent {
        data class Click(
            val screen: Boolean,
            val button: Int,
            val mouse: Vec2I =
                Vec2I(
                    lwjglMouse.getX(),
                    MC.displayHeight - lwjglMouse.getY() - 1,
                ),
        ) : Mouse
    }
}
