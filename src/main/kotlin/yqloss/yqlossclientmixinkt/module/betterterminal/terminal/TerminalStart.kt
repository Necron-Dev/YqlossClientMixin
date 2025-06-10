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
import yqloss.yqlossclientmixinkt.util.trimStyle

private val REGEX = Regex("What starts with: '(.)'\\?")

private val SLOTS = rectSlots(1, 1, 7, 3)

data class TerminalStart(
    val letter: String,
) : Terminal {
    override val enableQueue = true
    override val enableRightClick = false
    override val lines = 4
    override val beginLine = 1
    override val chestLines = 5
    override val title = "What starts with: '$letter'?"

    override fun parse(items: List<ItemStack?>): List<Int>? {
        return mapSlots(items, SLOTS, false) {
            if (it.displayName.trimStyle.uppercase().startsWith(letter)) {
                if (it.isItemEnchanted) -1 else 1
            } else {
                0
            }
        }
    }

    private fun getSlot(state: Int): SlotType {
        return when (state) {
            1 -> SlotType.START_CORRECT
            -1 -> SlotType.START_CLICKED
            else -> SlotType.START_WRONG
        }
    }

    override fun draw(state: List<Int>): List<Terminal.SlotRenderInfo> {
        return buildList {
            repeat(10) { add(SlotType.EMPTY) }
            (0..6).forEach { add(getSlot(state[it])) }
            repeat(2) { add(SlotType.EMPTY) }
            (7..13).forEach { add(getSlot(state[it])) }
            repeat(2) { add(SlotType.EMPTY) }
            (14..20).forEach { add(getSlot(state[it])) }
            repeat(1) { add(SlotType.EMPTY) }
        }
    }

    override fun predict(
        state: List<Int>,
        slotID: Int,
        button: Int,
    ): Terminal.Prediction {
        val pos =
            when (slotID) {
                in 10..16 -> slotID - 10
                in 19..25 -> slotID - 12
                in 28..34 -> slotID - 14
                else -> return Terminal.Prediction(state, ClickType.NONE, button)
            }
        val result = state.toMutableList()
        return Terminal.Prediction(
            result,
            when (result[pos]) {
                1 -> {
                    result[pos] = -1
                    ClickType.CORRECT
                }

                -1 -> ClickType.WRONG_WITHOUT_WINDOW_ID_UPDATE

                else -> ClickType.FAIL
            },
            button,
        )
    }

    companion object : TerminalFactory<TerminalStart> {
        override fun createIfMatch(title: String): TerminalStart? {
            if (!BetterTerminal.options.startEnabled) return null
            return REGEX.matchEntire(title)?.let { result ->
                TerminalStart(result.groupValues[1].uppercase())
            }
        }
    }
}
