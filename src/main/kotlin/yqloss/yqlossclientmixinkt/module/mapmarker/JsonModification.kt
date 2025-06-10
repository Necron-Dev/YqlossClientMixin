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

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.world.IBlockAccess
import net.yqloss.uktil.math.*
import net.yqloss.uktil.scope.noExcept
import yqloss.yqlossclientmixinkt.YCJson
import yqloss.yqlossclientmixinkt.util.*
import java.io.File

class JsonModification(
    private val ranges: MutableList<Pair<Area3I, ReplaceRule>>,
    val saveCallback: (MutableList<Pair<Area3I, ReplaceRule>>) -> Unit,
) : Modification {
    @Serializable
    data class BlockWithMeta(
        val blockID: String,
        val meta: Int,
    ) {
        val block: Block by lazy { Block.getBlockFromName(blockID) }

        val blockState: IBlockState by lazy { block.getStateFromMeta(meta) }

        fun match(other: BlockWithMeta) = block === other.block && (meta == Int.MIN_VALUE || meta == other.meta)

        fun transform(other: BlockWithMeta) = if (meta == Int.MIN_VALUE) copy(meta = other.meta) else this
    }

    @Serializable
    data class ReplaceRule(
        val block: BlockWithMeta,
        val conditions: Set<BlockWithMeta>,
        val replaceMode: Boolean,
    ) {
        val transformer by lazy {
            func@{ blockInfo: BlockWithMeta ->
                return@func if (replaceMode xor conditions.any { it.match(blockInfo) }) {
                    blockInfo
                } else {
                    block.transform(blockInfo)
                }
            }
        }
    }

    private val blockCache = Cache3D<MutableList<(Vec3I, BlockWithMeta) -> BlockWithMeta>>()
    private val areaCache = mutableListOf<(Vec3I, BlockWithMeta) -> BlockWithMeta>()
    private val subChunkCache = mutableSetOf<Vec3I>()
    private val subChunkRangeCache = mutableSetOf<Area3I>()
    private var area =
        Vec3I(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE) to Vec3I(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)

    init {
        buildCache()
    }

    private val Vec3I.subChunk get() = Vec3I(x shr 4, y shr 4, z shr 4)

    override fun containsSubChunk(chunk: Area3I): Boolean {
        val chunkPos = chunk.first.subChunk
        return subChunkRangeCache.any { chunkPos in it } || chunkPos in subChunkCache
    }

    private fun buildCache() {
        blockCache.clear()
        areaCache.clear()
        var areaMin = Vec3I(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE)
        var areaMax = Vec3I(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)

        ranges.forEach { range ->
            val transformer = range.second.transformer
            areaMin = areaMin min range.first.first
            areaMax = areaMax max range.first.second
            if (range.first.volume3I >= 65536) {
                areaCache.add { pos, blockInfo -> if (pos in range.first) transformer(blockInfo) else blockInfo }
            } else {
                range.first.iterable3I.forEach {
                    blockCache.getOrSet(it) { mutableListOf() }.add { _, blockInfo -> transformer(blockInfo) }
                    subChunkCache += it.subChunk
                }
            }
            val subChunkRange = range.first.first.subChunk to range.first.second.subChunk + Vec3I(1, 1, 1)
            if (subChunkRange.volume3I >= 256) {
                subChunkRangeCache += subChunkRange
            } else {
                subChunkRange.iterable3I.forEach { subChunkCache += it }
            }
        }

        area = areaMin to areaMax
    }

    private fun parsePos(arg: String): Vec3I {
        val split = arg.split(",")
        val relativeOrigin = MC.thePlayer.renderPos.asFloorVec3I
        val aimingOrigin = MC.objectMouseOver?.blockPos?.asVec3I
        val aimingDirection = MC.objectMouseOver
            ?.sideHit
            ?.directionVec
            ?.asVec3I
            ?: Vec3I(0, 0, 0)

        fun parse(
            num: String,
            relative: Int,
            aiming: Int?,
        ): Int {
            num.isEmpty() && return aiming ?: relative
            return when (num[0]) {
                '~' -> (num.substring(1).toIntOrNull() ?: 0) + relative
                '`' -> (num.substring(1).toIntOrNull() ?: 0) + aiming!!
                else -> num.toInt()
            }
        }

        return Vec3I(
            parse(split[0], relativeOrigin.x, aimingOrigin?.x),
            parse(split[1], relativeOrigin.y, aimingOrigin?.y),
            parse(split[2], relativeOrigin.z, aimingOrigin?.z),
        ) + aimingDirection * (split.getOrNull(3)?.toInt() ?: 0)
    }

    private fun parseBlock(arg: String): BlockWithMeta {
        val split = arg.split("@")
        return BlockWithMeta(
            split[0],
            if (split.size > 1) {
                when (val metaString = split[1]) {
                    "*" -> Int.MIN_VALUE
                    else -> metaString.toInt()
                }
            } else {
                0
            },
        ).apply { blockState }
    }

    private fun parseRule(args: List<String>): ReplaceRule {
        return ReplaceRule(
            parseBlock(args[0]),
            if (args.size > 2) {
                args.drop(2).map(::parseBlock).toSet()
            } else {
                emptySet()
            },
            when (args.getOrNull(1)) {
                "r", "replace" -> true
                else -> false
            },
        )
    }

    override fun onCommand(args: List<String>) {
        when (args[0]) {
            "s", "set" -> {
                val pos = parsePos(args[1])
                ranges.add((pos to pos + Vec3I(1, 1, 1)) to parseRule(args.subList(2, args.size)))
                buildCache()
                saveCallback(ranges)
                updateWorldRenderBlock(pos)
            }

            "f", "fill" -> {
                val pos1 = parsePos(args[1])
                val pos2 = parsePos(args[2])
                val posMin = pos1 min pos2
                val posMax = pos1 max pos2
                val area = posMin to posMax + Vec3I(1, 1, 1)
                ranges.add(area to parseRule(args.subList(3, args.size)))
                buildCache()
                saveCallback(ranges)
                updateWorldRenderArea(area)
            }

            "c", "clear" -> {
                ranges.clear()
                buildCache()
                saveCallback(ranges)
                updateWorldRender()
            }

            "rc", "removecontaining" -> {
                val pos = parsePos(args[1])
                ranges.removeIf { pos in it.first }
                buildCache()
                saveCallback(ranges)
                updateWorldRender()
            }
        }
    }

    override fun invoke(
        blockPos: Vec3I,
        blockState: IBlockState,
        blockAccess: IBlockAccess,
    ): IBlockState? {
        blockPos in area || return null
        return (areaCache + (blockCache[blockPos] ?: emptyList())).run {
            val blockWithMeta =
                BlockWithMeta(
                    blockState.block.registryName,
                    blockState.block.getMetaFromState(blockState),
                )
            var newBlockWithMeta = blockWithMeta
            forEach { newBlockWithMeta = it(blockPos, newBlockWithMeta) }
            newBlockWithMeta.takeIf { newBlockWithMeta !== blockWithMeta }?.blockState
        }
    }

    companion object {
        @Serializable
        data class RuleData(
            val range: Area3I,
            val rule: ReplaceRule,
        )

        @Serializable
        data class JsonData(
            val rules: List<RuleData>,
        )

        fun fromFile(file: File): JsonModification {
            var ranges = mutableListOf<Pair<Area3I, ReplaceRule>>()
            noExcept(MapMarker.logger::catching) {
                YCJson
                    .decodeFromString<JsonData>(file.readText(charset = Charsets.UTF_8))
                    .also { data ->
                        ranges = data.rules.map { it.range to it.rule }.toMutableList()
                    }
            }
            return JsonModification(ranges) { ranges ->
                file.parentFile.mkdirs()
                file.writeText(
                    YCJson.encodeToString(JsonData(ranges.map { RuleData(it.first, it.second) })),
                    charset = Charsets.UTF_8,
                )
            }
        }
    }
}
