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
import net.yqloss.uktil.event.EventRegistry
import net.yqloss.uktil.event.register
import net.yqloss.uktil.extension.int
import net.yqloss.uktil.functional.plus
import net.yqloss.uktil.math.*
import net.yqloss.uktil.scope.longRet
import net.yqloss.uktil.scope.noExcept
import yqloss.yqlossclientmixinkt.YC
import yqloss.yqlossclientmixinkt.api.YCTemplate
import yqloss.yqlossclientmixinkt.api.setDefault
import yqloss.yqlossclientmixinkt.event.hypixel.YCHypixelEvent
import yqloss.yqlossclientmixinkt.event.minecraft.YCMinecraftEvent
import yqloss.yqlossclientmixinkt.event.minecraft.YCRenderEvent
import yqloss.yqlossclientmixinkt.module.*
import yqloss.yqlossclientmixinkt.module.option.blockState
import yqloss.yqlossclientmixinkt.module.option.invoke
import yqloss.yqlossclientmixinkt.util.*
import kotlin.math.max
import kotlin.math.min

val INFO_MINING_PREDICTION = moduleInfo<MiningPredictionOptions>("mining_prediction", "Mining Prediction")

object MiningPrediction : YCModuleBase<MiningPredictionOptions>(INFO_MINING_PREDICTION) {
    private data class BlockInfo(
        val block: Ore,
        val blockPos: Vec3I,
        val time: Long,
    )

    private val destroyedBlocks = mutableMapOf<Vec3I, BlockInfo>()
    private var breakingPos = Vec3I(0, 0, 0)
    private var breakingTime = 0

    var breakingBlock: Ore? = null
        private set
    var breakingProgress = Fraction.ZERO
        private set
    var miningSpeed = 0
        private set
    var isAvailable = false
        private set

    fun getTicks(): Int {
        isAvailable || return 0
        val block = breakingBlock ?: return 0
        val modifiedMiningSpeed = block.type.modifyMiningSpeed(miningSpeed)
        return max(0, block.getTicksActual(modifiedMiningSpeed) + options.durationOffset)
    }

    fun getProgressPerTick(): Fraction {
        val ticks = getTicks()
        return if (ticks == 0) Fraction.ZERO else 1 over ticks
    }

    fun setPlaceholders(template: YCTemplate) {
        template.setDefault()
        isAvailable || return
        val block = breakingBlock ?: return
        val modifiedMiningSpeed = block.type.modifyMiningSpeed(miningSpeed)
        val ticks = max(0, block.getTicksActual(modifiedMiningSpeed) + options.durationOffset)
        template["pos"] = breakingPos
        template["ore"] = block
        template["progress"] = breakingProgress
        template["normalizedProgress"] = if (ticks == 0) 0 else (breakingProgress * ticks.frac).bigInt.int
        template["percentage"] = if (ticks == 0) 0.0 else breakingProgress.double * 100.0
        template["duration"] = breakingTime
        template["miningSpeed"] = modifiedMiningSpeed
        template["originalMiningSpeed"] = miningSpeed
        template["ticks"] = ticks
        template["unmodifiedTicks"] = block.getTicksActual(modifiedMiningSpeed)
        template["originalTicks"] = block.getTicksOriginal(modifiedMiningSpeed)
        template["instaMine"] = block.canInstaMine(modifiedMiningSpeed)
    }

    private fun resetBreaking() {
        breakingBlock = null
        breakingProgress = Fraction.ZERO
        breakingTime = 0
    }

    private fun reset() {
        destroyedBlocks.clear()
        isAvailable = false
        resetBreaking()
    }

    private fun getOre(pos: Vec3I): Ore? {
        val blockPos = pos.asBlockPos
        return Ore.getByBlockState(
            MC.theWorld.getBlockState(blockPos).block,
            MC.theWorld.getChunkFromBlockCoords(blockPos).getBlockMetadata(blockPos),
        )
    }

    private fun breakBlock() {
        val block = breakingBlock ?: return
        options.onBreakBlock(logger, ::setPlaceholders)
        YC.eventDispatcher(MiningPredictionEvent.BreakBlock)
        destroyedBlocks[breakingPos] = BlockInfo(block, breakingPos, System.nanoTime())
        updateWorldRenderBlock(breakingPos)
        resetBreaking()
    }

    private fun check(accumulateProgress: Boolean) {
        val block = breakingBlock ?: return
        val modifiedMiningSpeed = block.type.modifyMiningSpeed(miningSpeed)
        if (modifiedMiningSpeed > 0) {
            val ticks = max(0, block.getTicksActual(modifiedMiningSpeed) + options.durationOffset)
            if (ticks == 0) {
                breakingProgress = Fraction.ONE
                breakBlock()
            } else if (accumulateProgress) {
                breakingProgress += 1 over ticks
            }
        }
        if (breakingProgress >= Fraction.ONE) breakBlock()
    }

    private fun removeOutdatedBlocks() {
        val time = System.nanoTime()
        destroyedBlocks.entries.removeIf { (_, info) ->
            (time - info.time >= 1000000000 || info.block !== getOre(info.blockPos)).also {
                if (it) updateWorldRenderBlock(info.blockPos)
            }
        }
    }

    fun printDebugInfo() {
        printChatTranslated("{module.mining_prediction.message.debug_info}") {
            this["isAvailable"] = isAvailable
            this["miningSpeed"] = miningSpeed
        }
    }

    override val registerEvents: EventRegistry.() -> Unit
        get() = {
            super.registerEvents(this)

            register<YCMinecraftEvent.LoadWorld.Pre> {
                reset()
            }

            register<YCMinecraftEvent.Tick.Pre> {
                isAvailable = false
                miningSpeed = 0

                enabled && inWorld || longRet
                options.forceEnabled || inSkyblockMode(SKYBLOCK_MINING_ISLANDS) || longRet

                MC.thePlayer.sendQueue.playerInfoMap.firstOrNull {
                    noExcept(logger::catching) {
                        val rawName = MC.ingameGUI.tabList.getPlayerName(it)
                        if ("Mining Speed" in rawName) {
                            miningSpeed = rawName.trimStyle.filter { c -> c.isDigit() }.toInt()
                            return@firstOrNull true
                        }
                    }
                    false
                }

                miningSpeed > 0 || longRet

                isAvailable = true

                check(options.useClientTick)
                removeOutdatedBlocks()
            }

            register<YCHypixelEvent.ServerTick> {
                enabled && !options.useClientTick && inWorld && isAvailable || longRet

                check(!options.useClientTick)
                removeOutdatedBlocks()
            }

            register<MiningPredictionEvent.Mining> { event ->
                enabled && isAvailable || longRet

                if (MC.thePlayer.heldItem?.item !in SKYBLOCK_MINING_TOOLS) {
                    resetBreaking()
                } else {
                    val ore = getOre(event.pos)
                    if (ore !== breakingBlock) {
                        resetBreaking()
                        breakingPos = event.pos
                        if (event.pos !in destroyedBlocks) {
                            breakingBlock = ore
                            check(false)
                            removeOutdatedBlocks()
                        }
                    }
                }
            }

            register<MiningPredictionEvent.NotMining> {
                resetBreaking()
            }

            register<MiningPredictionEvent.RenderBlockDamage> { event ->
                enabled && isAvailable || longRet

                removeOutdatedBlocks()

                breakingBlock?.let {
                    event.mutableDamages.entries.removeIf { (_, damage) ->
                        damage.position.asVec3I == breakingPos
                    }

                    event.mutableDamages[MC.thePlayer.entityId] = DestroyBlockProgress(
                        0,
                        breakingPos.asBlockPos,
                    ).apply { partialBlockDamage = max(0, min(10, (10.0 * breakingProgress.double).int)) }
                }
            }

            register<YCRenderEvent.Block.ProcessAreaBlockState> { event ->
                enabled && isAvailable || longRet

                if (destroyedBlocks.keys.any { it in event.area }) {
                    event.mutableProcessor += { args ->
                        destroyedBlocks[args.position]?.let { info ->
                            if (getOre(info.blockPos) === info.block) {
                                args.mutableBlockState = options.destroyedBlock.blockState
                            }
                        }
                        Unit
                    }
                }
            }
        }
}
