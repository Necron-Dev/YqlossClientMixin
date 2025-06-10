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

package yqloss.yqlossclientmixinkt.module.mapmarker

import net.minecraft.block.state.IBlockState
import net.minecraft.world.IBlockAccess
import net.yqloss.uktil.math.Area3I
import net.yqloss.uktil.math.Vec3I

interface Modification : (Vec3I, IBlockState, IBlockAccess) -> IBlockState? {
    fun onTick() {}

    fun onCommand(args: List<String>)

    fun containsSubChunk(chunk: Area3I): Boolean
}
