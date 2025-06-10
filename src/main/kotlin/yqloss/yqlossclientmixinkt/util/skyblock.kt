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

@file:Suppress("NOTHING_TO_INLINE")

package yqloss.yqlossclientmixinkt.util

import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.yqloss.uktil.scope.noExcept
import java.util.*

inline val ItemStack?.skyBlockID: String?
    get() = this?.run {
        noExcept {
            this.tagCompound.getCompoundTag("ExtraAttributes").getString("id")
        }
    }

inline val ItemStack?.skyBlockUUID: UUID?
    get() = this?.run {
        noExcept {
            UUID.fromString(this.tagCompound.getCompoundTag("ExtraAttributes").getString("uuid"))
        }
    }

val SKYBLOCK_MINING_TOOLS by lazy {
    setOf(
        Items.wooden_pickaxe,
        Items.stone_pickaxe,
        Items.golden_pickaxe,
        Items.iron_pickaxe,
        Items.diamond_pickaxe,
        Items.skull,
        Items.prismarine_shard,
    )
}
