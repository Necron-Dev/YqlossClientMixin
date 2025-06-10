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

package yqloss.yqlossclientmixinkt.impl.mixincallback

import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.BlockRendererDispatcher
import net.minecraft.client.renderer.WorldRenderer
import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockPos
import net.minecraft.world.IBlockAccess
import net.yqloss.uktil.accessor.getValue
import net.yqloss.uktil.accessor.outs.Box
import net.yqloss.uktil.accessor.outs.inBox
import net.yqloss.uktil.accessor.outs.value
import net.yqloss.uktil.accessor.refs.threadLocal
import net.yqloss.uktil.accessor.setValue
import net.yqloss.uktil.generic.castTo
import net.yqloss.uktil.math.Vec3I
import net.yqloss.uktil.math.contains
import yqloss.yqlossclientmixinkt.YC
import yqloss.yqlossclientmixinkt.event.minecraft.YCRenderEvent
import yqloss.yqlossclientmixinkt.impl.option.YqlossClientConfig
import yqloss.yqlossclientmixinkt.util.asVec3I
import yqloss.yqlossclientmixinkt.util.printError
import java.lang.ref.WeakReference

object CallbackRenderChunk {
    object YqlossClient {
        class Cache<T>(
            val origin: Vec3I,
        ) {
            val cacheVersion = IntArray(972) { -2147483648 }
            val cacheData = MutableList<T?>(972) { null }
            var z = 0

            fun getIndex(
                vec: Vec3I,
                z: Int,
            ) = vec.x + vec.y * 18 + z * 324 + 19

            fun setCurrentZ(zIn: Int) {
                z = zIn - origin.z
            }

            inline fun getOrSet(
                vec: Vec3I,
                function: (Vec3I) -> T,
            ): T {
                val diff = vec - origin
                diff in (Vec3I(-1, -1, z - 1) areaTo Vec3I(17, 17, z + 2)) || return function(vec)
                val version = diff.z
                val index = getIndex(diff, (version % 3 + 3) % 3)
                if (cacheVersion[index] != version) {
                    cacheVersion[index] = version
                    cacheData[index] = function(vec)
                }
                return cacheData[index].castTo()
            }
        }

        class YCBlockAccess(
            val blockAccess: IBlockAccess,
            val origin: Vec3I,
            var blockStateProcessor: Box<((YCRenderEvent.Block.ProcessAreaBlockState.Parameters) -> Unit)?>? = null,
            var tileEntityProcessor: Box<((YCRenderEvent.Block.ProcessAreaTileEntity.Parameters) -> Unit)?>? = null,
        ) : IBlockAccess by blockAccess {
            val cacheTileEntity = Cache<TileEntity?>(origin)
            val cacheBlockState = Cache<IBlockState?>(origin)

            override fun getTileEntity(pos: BlockPos): TileEntity? {
                return cacheTileEntity.getOrSet(pos.asVec3I) {
                    YCRenderEvent.Block.ProcessAreaTileEntity
                        .Parameters(
                            pos.asVec3I,
                            blockAccess.getTileEntity(pos),
                        ).also { tileEntityProcessor?.value?.invoke(it) }
                        .mutableTileEntity
                }
            }

            override fun getBlockState(pos: BlockPos): IBlockState {
                val state = blockAccess.getBlockState(pos) ?: run {
                    printError("${this::class} getBlockState null at $pos")
                    Blocks.air.defaultState
                }
                return cacheBlockState
                    .getOrSet(pos.asVec3I) {
                        YCRenderEvent.Block.ProcessAreaBlockState
                            .Parameters(
                                pos.asVec3I,
                                state,
                            ).also { blockStateProcessor?.value?.invoke(it) }
                            .mutableBlockState
                    } ?: state
            }

            override fun isAirBlock(pos: BlockPos) = getBlockState(pos).block.material === Material.air
        }

        interface BlockAccessWrapper {
            val wrappedBlockAccess: IBlockAccess?
        }

        data class Wrapper(
            var originalBlockAccess: IBlockAccess? = null,
            var wrappedBlockAccess: YCBlockAccess? = null,
            var origin: Vec3I = Vec3I(0, 0, 0),
        ) {
            private fun unwrap(blockAccess: IBlockAccess): IBlockAccess {
                return if (blockAccess is BlockAccessWrapper) {
                    blockAccess.wrappedBlockAccess?.also(::unwrap) ?: blockAccess
                } else {
                    blockAccess
                }
            }

            fun wrap(blockAccessIn: IBlockAccess): YCBlockAccess {
                val blockAccess = unwrap(blockAccessIn)
                return wrappedBlockAccess?.takeIf { originalBlockAccess === blockAccess } ?: YCBlockAccess(
                    blockAccess,
                    origin,
                ).also {
                    originalBlockAccess = blockAccess
                    wrappedBlockAccess = it
                }
            }
        }

        var wrapperRef by threadLocal { WeakReference(Wrapper()) }

        fun rebuildChunkPre(
            wrapper: Wrapper,
            position: BlockPos,
        ) {
            wrapperRef = WeakReference(wrapper)
            wrapper.wrappedBlockAccess = null
            wrapper.origin = position.asVec3I
        }

        private fun YCBlockAccess.collectProcessors() {
            blockStateProcessor ?: run {
                blockStateProcessor =
                    YCRenderEvent.Block
                        .ProcessAreaBlockState(blockAccess, origin to origin + Vec3I(16, 16, 16))
                        .also(YC.eventDispatcher)
                        .mutableProcessor.inBox
            }
            tileEntityProcessor ?: run {
                tileEntityProcessor =
                    YCRenderEvent.Block
                        .ProcessAreaTileEntity(blockAccess, origin to origin + Vec3I(16, 16, 16))
                        .also(YC.eventDispatcher)
                        .mutableProcessor.inBox
            }
        }

        fun rebuildChunkGetBlockState(
            wrapper: Wrapper,
            blockAccess: IBlockAccess,
            blockPos: BlockPos,
        ): IBlockState {
            if (YqlossClientConfig.main.disableBlockAccess) {
                return blockAccess.getBlockState(blockPos)
            }
            return wrapper
                .wrap(blockAccess)
                .apply {
                    collectProcessors()
                    cacheBlockState.setCurrentZ(blockPos.z)
                }.getBlockState(blockPos)
        }

        fun rebuildChunkGetTileEntity(
            wrapper: Wrapper,
            blockAccess: IBlockAccess,
            blockPos: BlockPos,
        ): TileEntity? {
            if (YqlossClientConfig.main.disableBlockAccess) {
                return blockAccess.getTileEntity(blockPos)
            }
            return wrapper
                .wrap(blockAccess)
                .apply {
                    collectProcessors()
                    cacheTileEntity.setCurrentZ(blockPos.z)
                }.getTileEntity(blockPos)
        }

        fun rebuildChunkRenderBlock(
            wrapper: Wrapper,
            dispatcher: BlockRendererDispatcher,
            blockState: IBlockState,
            blockPos: BlockPos,
            blockAccess: IBlockAccess,
            worldRenderer: WorldRenderer,
        ): Boolean {
            if (YqlossClientConfig.main.disableBlockAccess) {
                return dispatcher.renderBlock(blockState, blockPos, blockAccess, worldRenderer)
            }
            return dispatcher.renderBlock(blockState, blockPos, wrapper.wrap(blockAccess), worldRenderer)
        }
    }
}
