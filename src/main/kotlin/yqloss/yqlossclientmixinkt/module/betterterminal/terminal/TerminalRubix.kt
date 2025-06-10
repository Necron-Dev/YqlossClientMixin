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

package yqloss.yqlossclientmixinkt.module.betterterminal.terminal

import net.minecraft.item.ItemStack
import yqloss.yqlossclientmixinkt.module.betterterminal.*
import kotlin.math.abs

private val SLOTS = rectSlots(3, 1, 5, 3)

data class TerminalRubix(
    val unit: Unit = Unit,
) : Terminal {
    override val enableQueue = true
    override val enableRightClick = true
    override val lines = 4
    override val beginLine = 1
    override val chestLines = 5
    override val title = "Change all to same color!"

    override fun parse(items: List<ItemStack?>) = mapSlots(items, SLOTS, true) {
        when (it.metadata) {
            14 -> 0
            1 -> 1
            4 -> 2
            13 -> 3
            11 -> 4
            else -> 0
        }
    }

    private fun clicksTo(
        from: Int,
        to: Int,
    ) = (to - from + 12) % 5 - 2

    private fun solve(state: List<Int>): Int {
        var solution = 0
        var clicks = Int.MAX_VALUE
        repeat(5) { color ->
            val colorClicks = state.sumOf { abs(clicksTo(it, color)) }
            if (colorClicks < clicks) {
                solution = color
                clicks = colorClicks
            }
        }
        return solution
    }

    private fun getSlot(
        state: Int,
        solution: Int,
    ): Terminal.SlotRenderInfo {
        return when (clicksTo(state, solution)) {
            -2 -> Terminal.SlotRenderInfo(SlotType.RUBIX_RIGHT_2, "-2")
            -1 -> Terminal.SlotRenderInfo(SlotType.RUBIX_RIGHT_1, "-1")
            0 -> Terminal.SlotRenderInfo(SlotType.RUBIX_CORRECT, null)
            1 -> Terminal.SlotRenderInfo(SlotType.RUBIX_LEFT_1, "1")
            2 -> Terminal.SlotRenderInfo(SlotType.RUBIX_LEFT_2, "2")
            else -> Terminal.SlotRenderInfo(SlotType.RUBIX_CORRECT, null)
        }.apply {
            BetterTerminal.options.rubixShowNumber || return copy(text = null)
        }
    }

    override fun draw(state: List<Int>): List<Terminal.SlotRenderInfo> = buildList {
        val solution = solve(state)
        repeat(12) { add(SlotType.EMPTY) }
        (0..2).forEach { add(getSlot(state[it], solution)) }
        repeat(6) { add(SlotType.EMPTY) }
        (3..5).forEach { add(getSlot(state[it], solution)) }
        repeat(6) { add(SlotType.EMPTY) }
        (6..8).forEach { add(getSlot(state[it], solution)) }
        repeat(3) { add(SlotType.EMPTY) }
    }

    override fun predict(
        state: List<Int>,
        slotID: Int,
        button: Int,
    ): Terminal.Prediction {
        val pos = when (slotID) {
            in 12..14 -> slotID - 12
            in 21..23 -> slotID - 18
            in 30..32 -> slotID - 24
            else -> return Terminal.Prediction(state, ClickType.NONE, button)
        }
        var offset = if (button == 1) -1 else 1
        var actualButton = button
        val clicksToSolution = clicksTo(state[pos], solve(state))
        if (BetterTerminal.options.rubixCorrectDirection && offset * clicksToSolution < 0) {
            offset = -offset
            actualButton = if (button == 1) 0 else 1
        }
        val wrong = offset * clicksToSolution <= 0
        val result = state.toMutableList()
        result[pos] = (result[pos] + offset + 5) % 5
        return Terminal.Prediction(
            result,
            if (wrong) ClickType.WRONG_WITH_WINDOW_ID_UPDATE else ClickType.CORRECT,
            actualButton,
        )
    }

    companion object : TerminalFactory<TerminalRubix> {
        override fun createIfMatch(title: String): TerminalRubix? {
            BetterTerminal.options.rubixEnabled && title == "Change all to same color!" || return null
            return TerminalRubix()
        }
    }
}
