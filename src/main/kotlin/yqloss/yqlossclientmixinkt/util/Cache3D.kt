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

package yqloss.yqlossclientmixinkt.util

import net.yqloss.uktil.math.Vec2I
import net.yqloss.uktil.math.Vec3I
import net.yqloss.uktil.math.contains

private const val NEAR_RANGE = 16
private val NEAR_OFFSET = Vec2I(-NEAR_RANGE / 2, -NEAR_RANGE / 2)
private val NEAR_AREA = Vec2I(0, 0) areaTo Vec2I(NEAR_RANGE, NEAR_RANGE)

class Cache3D<T> {
    private val nearChunks = MutableList<MutableList<T?>?>(NEAR_RANGE * NEAR_RANGE) { null }
    private val farChunks = mutableMapOf<Vec2I, MutableList<T?>>()

    fun clear() {
        repeat(nearChunks.size) { nearChunks[it] = null }
        farChunks.clear()
    }

    private fun transformPos(pos: Vec3I): Pair<Vec2I, Int> {
        return Vec2I(pos.x shr 4, pos.z shr 4) to ((pos.x and 15) or (pos.z and 15 shl 4) or (pos.y shl 8))
    }

    private fun getNearIndex(chunk: Vec2I): Int? {
        val offset = chunk + NEAR_OFFSET
        return if (offset in NEAR_AREA) {
            offset.x + offset.y * NEAR_RANGE
        } else {
            null
        }
    }

    private fun newChunk(): MutableList<T?> = MutableList(65536) { null }

    private fun getChunk(chunk: Vec2I) = getNearIndex(chunk)?.let { nearChunks[it] } ?: farChunks[chunk]

    private fun getChunkOrCreate(chunk: Vec2I): MutableList<T?> {
        return getNearIndex(chunk)?.let { i ->
            nearChunks[i] ?: newChunk().also {
                nearChunks[i] = it
            }
        } ?: farChunks.getOrPut(chunk, ::newChunk)
    }

    operator fun get(index: Vec3I): T? {
        index.y in 0..255 || return null
        return transformPos(index).let { (chunk, pos) ->
            getChunk(chunk)?.let { chunkData ->
                chunkData[pos]
            }
        }
    }

    operator fun set(
        index: Vec3I,
        value: T,
    ) {
        index.y in 0..255 || return
        transformPos(index).let { (chunk, pos) ->
            getChunkOrCreate(chunk).let { chunkData ->
                chunkData[pos] = value
            }
        }
    }

    inline fun getOrSet(
        index: Vec3I,
        function: (Vec3I) -> T,
    ) = get(index) ?: function(index).also { set(index, it) }
}
