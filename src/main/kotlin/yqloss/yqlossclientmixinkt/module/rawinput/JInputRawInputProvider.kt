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

import net.java.games.input.Controller
import net.java.games.input.ControllerEnvironment
import net.java.games.input.Mouse
import net.yqloss.uktil.functional.intervalAction
import net.yqloss.uktil.scope.noExcept
import yqloss.yqlossclientmixinkt.module.enabled
import yqloss.yqlossclientmixinkt.util.MC
import yqloss.yqlossclientmixinkt.ycLogger

object JInputRawInputProvider : RawInputProvider {
    private val logger = ycLogger("JInput Raw Input Provider")

    private var savedMouse: Mouse? = null

    private val findMouse = intervalAction(1000000000L) {
        logger.info("trying to find a mouse")

        noExcept(logger::catching) {
            for (controller in ControllerEnvironment.getDefaultEnvironment().controllers) {
                noExcept(logger::catching) {
                    if (controller.type === Controller.Type.MOUSE) {
                        val mouse = controller as Mouse
                        mouse.poll()
                        if (mouse.x.pollData !in -0.1..0.1 || mouse.y.pollData !in -0.1..0.1) {
                            savedMouse = mouse
                            logger.info("found mouse $mouse")
                            return@intervalAction
                        }
                    }
                }
            }
        }

        logger.info("failed to find a mouse")
    }

    override fun poll() {
        RawInput.enabled || return

        savedMouse ?: findMouse()

        savedMouse?.let { mouse ->
            mouse.poll()
            MC.currentScreen ?: run {
                RawInput.x += mouse.x.pollData
                RawInput.y += mouse.y.pollData
            }
        }
    }
}
