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

import net.minecraft.client.multiplayer.WorldClient
import yqloss.yqlossclientmixinkt.event.YCEvent

sealed interface YCMinecraftEvent : YCEvent {
    sealed interface Load : YCMinecraftEvent {
        data object Post : Load
    }

    sealed interface Loop : YCMinecraftEvent {
        data object Pre : Loop
    }

    sealed interface Tick : YCMinecraftEvent {
        data object Pre : Tick
    }

    sealed interface LoadWorld : YCMinecraftEvent {
        data class Pre(
            val world: WorldClient?,
        ) : LoadWorld
    }
}
