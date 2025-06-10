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

package yqloss.yqlossclientmixinkt.module.miningprediction

import net.minecraft.client.renderer.DestroyBlockProgress
import net.yqloss.uktil.event.Event
import net.yqloss.uktil.math.Vec3I

sealed interface MiningPredictionEvent : Event {
    data class Mining(
        val pos: Vec3I,
    ) : MiningPredictionEvent

    data object NotMining : MiningPredictionEvent

    data class RenderBlockDamage(
        val damages: Map<Int, DestroyBlockProgress>,
        var mutableDamages: MutableMap<Int, DestroyBlockProgress> = damages.toMutableMap(),
    ) : MiningPredictionEvent

    data object BreakBlock : MiningPredictionEvent
}
