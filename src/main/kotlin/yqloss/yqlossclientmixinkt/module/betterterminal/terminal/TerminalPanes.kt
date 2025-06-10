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

private val SLOTS = rectSlots(2, 1, 6, 3)

data class TerminalPanes(
    val unit: Unit = Unit,
) : Terminal {
    override val enableQueue = true
    override val enableRightClick = false
    override val lines = 4
    override val beginLine = 1
    override val chestLines = 5
    override val title = "Correct all the panes!"

    override fun parse(items: List<ItemStack?>) = mapSlots(items, SLOTS, true) {
        if (it.metadata == 5) 1 else 0
    }

    private fun getSlot(state: Int) = if (state == 0) SlotType.PANES_OFF else SlotType.PANES_ON

    override fun draw(state: List<Int>): List<Terminal.SlotRenderInfo> = buildList {
        repeat(11) { add(SlotType.EMPTY) }
        (0..4).forEach { add(getSlot(state[it])) }
        repeat(4) { add(SlotType.EMPTY) }
        (5..9).forEach { add(getSlot(state[it])) }
        repeat(4) { add(SlotType.EMPTY) }
        (10..14).forEach { add(getSlot(state[it])) }
        repeat(2) { add(SlotType.EMPTY) }
    }

    override fun predict(
        state: List<Int>,
        slotID: Int,
        button: Int,
    ): Terminal.Prediction {
        val pos = when (slotID) {
            in 11..15 -> slotID - 11
            in 20..24 -> slotID - 15
            in 29..33 -> slotID - 19
            else -> return Terminal.Prediction(state, ClickType.NONE, button)
        }
        val wrong = state[pos] != 0
        val result = state.toMutableList()
        result[pos] = if (result[pos] == 0) 1 else 0
        return Terminal.Prediction(
            result,
            if (wrong) ClickType.WRONG_WITH_WINDOW_ID_UPDATE else ClickType.CORRECT,
            button,
        )
    }

    companion object : TerminalFactory<TerminalPanes> {
        override fun createIfMatch(title: String): TerminalPanes? {
            BetterTerminal.options.panesEnabled && title == "Correct all the panes!" || return null
            return TerminalPanes()
        }
    }
}
