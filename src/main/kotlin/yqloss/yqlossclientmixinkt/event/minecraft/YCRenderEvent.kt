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

import net.minecraft.block.state.IBlockState
import net.minecraft.client.gui.GuiScreen
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.IBlockAccess
import net.yqloss.uktil.event.Event
import net.yqloss.uktil.math.Area3I
import net.yqloss.uktil.math.Vec3I

sealed interface YCRenderEvent : Event {
    sealed interface Render : YCRenderEvent {
        data object Pre : Render
    }

    sealed interface Entity : YCRenderEvent {
        data object Post : Entity
    }

    sealed interface Screen : YCRenderEvent {
        data class Proxy(
            val screen: GuiScreen,
            var mutableScreen: GuiScreen? = null,
        ) : Screen
    }

    sealed interface Block : YCRenderEvent {
        // you must not modify an air block
        // be careful when modifying a block to a tile entity or vice versa
        data class ProcessAreaBlockState(
            val blockAccess: IBlockAccess,
            val area: Area3I,
            var mutableProcessor: ((Parameters) -> Unit)? = null,
        ) : Block {
            data class Parameters(
                val position: Vec3I,
                val blockState: IBlockState,
                var mutableBlockState: IBlockState = blockState,
            )
        }

        // you must not modify an air block
        // be careful when modifying a block to a tile entity or vice versa
        data class ProcessAreaTileEntity(
            val blockAccess: IBlockAccess,
            val area: Area3I,
            var mutableProcessor: ((Parameters) -> Unit)? = null,
        ) : Block {
            data class Parameters(
                val position: Vec3I,
                val tileEntity: TileEntity?,
                var mutableTileEntity: TileEntity? = tileEntity,
            )
        }
    }
}
