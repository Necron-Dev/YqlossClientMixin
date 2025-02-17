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

package yqloss.yqlossclientmixinkt.module.betterterminal

import net.minecraft.client.gui.GuiScreen
import yqloss.yqlossclientmixinkt.event.YCCancelableEvent
import yqloss.yqlossclientmixinkt.event.YCEvent

sealed interface BetterTerminalEvent : YCEvent {
    data class DrawDefaultBackground(
        val screen: GuiScreen,
        override var canceled: Boolean = false,
    ) : BetterTerminalEvent,
        YCCancelableEvent

    data class RenderTooltip(
        val screen: GuiScreen,
        override var canceled: Boolean = false,
    ) : BetterTerminalEvent,
        YCCancelableEvent
}
