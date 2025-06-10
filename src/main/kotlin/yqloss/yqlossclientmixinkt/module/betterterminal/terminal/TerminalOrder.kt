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

private val SLOTS = rectSlots(1, 1, 7, 2)

data class TerminalOrder(
    val unit: Unit = Unit,
) : Terminal {
    override val enableQueue = true
    override val enableRightClick = false
    override val lines = 3
    override val beginLine = 1
    override val chestLines = 4
    override val title = "Click in order!"

    override fun parse(items: List<ItemStack?>) = mapSlots(items, SLOTS, true) {
        if (it.metadata == 5) -it.stackSize else it.stackSize
    }

    private fun solve(state: List<Int>) = state.filter { it > 0 }.minOrNull() ?: 15

    private fun getSlot(
        state: Int,
        solution: Int,
    ): Terminal.SlotRenderInfo {
        val text = abs(state).toString()
        return if (state < 0) {
            Terminal.SlotRenderInfo(
                SlotType.ORDER_CLICKED,
                if (BetterTerminal.options.orderShowClickedNumber) {
                    text
                } else {
                    null
                },
            )
        } else {
            when (state - solution) {
                0 -> Terminal.SlotRenderInfo(SlotType.ORDER_1, text)
                1 -> Terminal.SlotRenderInfo(SlotType.ORDER_2, text)
                2 -> Terminal.SlotRenderInfo(SlotType.ORDER_3, text)
                else -> Terminal.SlotRenderInfo(SlotType.ORDER_OTHER, text)
            }.apply {
                BetterTerminal.options.orderShowNumber || return copy(text = null)
            }
        }
    }

    override fun draw(state: List<Int>): List<Terminal.SlotRenderInfo> = buildList {
        val solution = solve(state)
        repeat(10) { add(SlotType.EMPTY) }
        (0..6).forEach { add(getSlot(state[it], solution)) }
        repeat(2) { add(SlotType.EMPTY) }
        (7..13).forEach { add(getSlot(state[it], solution)) }
        repeat(1) { add(SlotType.EMPTY) }
    }

    override fun predict(
        state: List<Int>,
        slotID: Int,
        button: Int,
    ): Terminal.Prediction {
        val pos = when (slotID) {
            in 10..16 -> slotID - 10
            in 19..25 -> slotID - 12
            else -> return Terminal.Prediction(state, ClickType.NONE, button)
        }
        val solution = solve(state)
        if (state[pos] != solution) return Terminal.Prediction(state, ClickType.WRONG_WITHOUT_WINDOW_ID_UPDATE, button)
        val result = state.toMutableList()
        result[pos] = -result[pos]
        return Terminal.Prediction(result, ClickType.CORRECT, button)
    }

    companion object : TerminalFactory<TerminalOrder> {
        override fun createIfMatch(title: String): TerminalOrder? {
            BetterTerminal.options.orderEnabled && title == "Click in order!" || return null
            return TerminalOrder()
        }
    }
}
