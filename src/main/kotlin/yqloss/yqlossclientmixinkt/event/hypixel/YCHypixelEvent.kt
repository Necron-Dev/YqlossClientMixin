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

package yqloss.yqlossclientmixinkt.event.hypixel

import net.yqloss.uktil.accessor.getValue
import net.yqloss.uktil.accessor.refs.trigger
import net.yqloss.uktil.event.Event
import net.yqloss.uktil.extension.double
import yqloss.yqlossclientmixinkt.util.frameCounter

var hypixelServerTickCounter = 0L

var hypixelServerTickUpdateTime = 0L

var hypixelServerTickDuration = 50_000_000L

val hypixelPartialServerTicks by trigger(::frameCounter) {
    ((System.nanoTime() - hypixelServerTickUpdateTime).double / hypixelServerTickDuration).coerceIn(0.0..1.0)
}

sealed interface YCHypixelEvent : Event {
    data object ServerTick : YCHypixelEvent
}
