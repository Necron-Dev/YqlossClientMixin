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

package yqloss.yqlossclientmixinkt.impl.module.cursor

import net.yqloss.uktil.event.EventRegistry
import net.yqloss.uktil.event.register
import net.yqloss.uktil.scope.longRet
import yqloss.yqlossclientmixinkt.impl.module.YCModuleImplBase
import yqloss.yqlossclientmixinkt.impl.nanovgui.GUIEvent
import yqloss.yqlossclientmixinkt.impl.option.module.CursorOptionsImpl
import yqloss.yqlossclientmixinkt.module.cursor.Cursor
import yqloss.yqlossclientmixinkt.module.enabled
import yqloss.yqlossclientmixinkt.module.register
import yqloss.yqlossclientmixinkt.util.MC
import yqloss.yqlossclientmixinkt.util.mousePosition

object CursorOverlay : YCModuleImplBase<CursorOptionsImpl, Cursor>(Cursor) {
    private var lastTime = 0L

    override val registerEvents: EventRegistry.() -> Unit = {
        super.registerEvents(this)

        register<GUIEvent.Screen.Post>(Int.MAX_VALUE - 1000) { event ->
            enabled || longRet

            if (MC.currentScreen === null) {
                ContinuousTrail.clear()
                longRet
            }

            val mouse = mousePosition
            val time = System.nanoTime()

            ContinuousTrail.render(event, mouse, time, lastTime, options.continuousOptions)

            lastTime = time
        }
    }

    init {
        register
    }
}
