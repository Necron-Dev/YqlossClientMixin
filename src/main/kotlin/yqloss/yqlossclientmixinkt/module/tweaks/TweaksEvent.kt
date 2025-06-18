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

package yqloss.yqlossclientmixinkt.module.tweaks

import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.yqloss.uktil.event.CancelableEvent
import net.yqloss.uktil.event.Event
import net.yqloss.uktil.math.Vec3I

sealed interface TweaksEvent : Event {
    data class SetAnglesPost(
        val entity: Entity,
    ) : TweaksEvent

    data class RightClickBlockPre(
        val world: WorldClient,
        val blockPos: Vec3I,
        override var canceled: Boolean = false,
    ) : TweaksEvent,
        CancelableEvent

    data class IsHittingPositionCheck(
        val pos: Vec3I?,
        val currentBlock: Vec3I?,
        val currentItemHittingBlock: ItemStack?,
        var returnValue: Boolean = false,
        override var canceled: Boolean = false,
    ) : TweaksEvent,
        CancelableEvent

    object RenderHUDPre : TweaksEvent
}
