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

private val SLOTS = rectSlots(1, 0, 5, 5)

data class TerminalAlign(
    val unit: Unit = Unit,
) : Terminal {
    override val enableQueue = false
    override val enableRightClick = false
    override val lines = 6
    override val beginLine = 0
    override val chestLines = 6
    override val title = "Click the button on time!"

    override fun parse(items: List<ItemStack?>): List<Int>? {
        return mapSlots(items, SLOTS, true) { it.metadata }?.let {
            buildList {
                add(it.take(5).indexOf(2))
                (0..4).firstOrNull { line ->
                    if (it[20 - 5 * line] != 0) {
                        add(3 - line)
                        add(it.slice(20 - 5 * line..24 - 5 * line).indexOf(5))
                        true
                    } else {
                        false
                    }
                }
            }.takeIf { l -> l.size == 3 && l.all { v -> v >= 0 } }
        }
    }

    override fun draw(state: List<Int>): List<Terminal.SlotRenderInfo> {
        return buildList {
            add(SlotType.EMPTY)
            repeat(state[0]) { add(SlotType.EMPTY) }
            add(SlotType.ALIGN_TARGET)
            repeat(7 - state[0]) { add(SlotType.EMPTY) }
            repeat(4) { line ->
                if (line == state[1]) {
                    add(SlotType.EMPTY)
                    repeat(state[2]) { add(SlotType.ALIGN_ACTIVE_OTHER) }
                    add(SlotType.ALIGN_ACTIVE_CURRENT)
                    repeat(4 - state[2]) { add(SlotType.ALIGN_ACTIVE_OTHER) }
                    add(SlotType.EMPTY)
                    add(SlotType.ALIGN_ACTIVE_BUTTON)
                    add(SlotType.EMPTY)
                } else {
                    add(SlotType.EMPTY)
                    repeat(5) { add(SlotType.ALIGN_INACTIVE) }
                    add(SlotType.EMPTY)
                    add(SlotType.ALIGN_INACTIVE_BUTTON)
                    add(SlotType.EMPTY)
                }
            }
            add(SlotType.EMPTY)
            repeat(state[0]) { add(SlotType.EMPTY) }
            add(SlotType.ALIGN_TARGET)
            repeat(7 - state[0]) { add(SlotType.EMPTY) }
        }
    }

    override fun predict(
        state: List<Int>,
        slotID: Int,
        button: Int,
    ) = Terminal.Prediction(state, ClickType.NORMAL, button)

    companion object : TerminalFactory<TerminalAlign> {
        override fun createIfMatch(title: String): TerminalAlign? {
            BetterTerminal.options.alignEnabled && title == "Click the button on time!" || return null
            return TerminalAlign()
        }
    }
}
