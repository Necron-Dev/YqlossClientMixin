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

import net.minecraft.init.Items
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.util.IStringSerializable
import yqloss.yqlossclientmixinkt.module.betterterminal.*

private val REGEX = Regex("Select all the (.*) items!")

private val SLOTS = rectSlots(1, 1, 7, 4)

data class TerminalColor(
    val meta: Int,
) : Terminal {
    override val enableQueue = true
    override val enableRightClick = false
    override val lines = 5
    override val beginLine = 1
    override val chestLines = 6
    override val title =
        "Select all the ${(EnumDyeColor.entries[meta] as IStringSerializable).name.replace("_", " ")} items!"

    private fun getColorMeta(itemStack: ItemStack) = when (itemStack.item) {
        Items.dye -> 15 - itemStack.itemDamage
        else -> itemStack.itemDamage
    }

    override fun parse(items: List<ItemStack?>) = mapSlots(items, SLOTS, false) {
        if (meta == getColorMeta(it)) {
            if (it.isItemEnchanted) -1 else 1
        } else {
            0
        }
    }

    private fun getSlot(state: Int) = when (state) {
        1 -> SlotType.COLOR_CORRECT
        -1 -> SlotType.COLOR_CLICKED
        else -> SlotType.COLOR_WRONG
    }

    override fun draw(state: List<Int>): List<Terminal.SlotRenderInfo> = buildList {
        repeat(10) { add(SlotType.EMPTY) }
        (0..6).forEach { add(getSlot(state[it])) }
        repeat(2) { add(SlotType.EMPTY) }
        (7..13).forEach { add(getSlot(state[it])) }
        repeat(2) { add(SlotType.EMPTY) }
        (14..20).forEach { add(getSlot(state[it])) }
        repeat(2) { add(SlotType.EMPTY) }
        (21..27).forEach { add(getSlot(state[it])) }
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
            in 28..34 -> slotID - 14
            in 37..43 -> slotID - 16
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

    companion object : TerminalFactory<TerminalColor> {
        private fun getMeta(match: String) = EnumDyeColor.entries
            .firstOrNull {
                match == (it as IStringSerializable).name.uppercase().replace("_", " ")
            }?.metadata

        override fun createIfMatch(title: String): TerminalColor? {
            BetterTerminal.options.colorEnabled || return null
            val result = REGEX.matchEntire(title) ?: return null
            val meta = getMeta(result.groupValues[1]) ?: return null
            return TerminalColor(meta)
        }
    }
}
