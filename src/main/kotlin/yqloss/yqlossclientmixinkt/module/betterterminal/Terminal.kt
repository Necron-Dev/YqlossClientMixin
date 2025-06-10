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

package yqloss.yqlossclientmixinkt.module.betterterminal

import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import yqloss.yqlossclientmixinkt.util.item

interface Terminal {
    val enableQueue: Boolean
    val enableRightClick: Boolean
    val lines: Int
    val beginLine: Int
    val chestLines: Int
    val title: String

    fun parse(items: List<ItemStack?>): List<Int>?

    fun draw(state: List<Int>): List<SlotRenderInfo>

    data class Prediction(
        val state: List<Int>,
        val clickType: ClickType,
        val button: Int,
    )

    fun predict(
        state: List<Int>,
        slotID: Int,
        button: Int,
    ): Prediction

    data class SlotRenderInfo(
        val type: SlotType,
        val text: String?,
    )

    fun MutableList<SlotRenderInfo>.add(type: SlotType) {
        add(SlotRenderInfo(type, null))
    }

    fun MutableList<SlotRenderInfo>.add(
        type: SlotType,
        text: String,
    ) {
        add(SlotRenderInfo(type, text))
    }
}

interface TerminalFactory<T : Terminal> {
    fun createIfMatch(title: String): T?
}

inline fun Terminal.mapSlots(
    items: List<ItemStack?>,
    range: List<Int>,
    glassPane: Boolean,
    function: (ItemStack) -> Int,
): List<Int>? {
    return range
        .mapNotNull { if (it in items.indices) items[it] else null }
        .filter { !glassPane || it.item === Blocks.stained_glass_pane.item }
        .takeIf { it.size == range.size }
        ?.map(function)
}

fun rectSlots(
    x1: Int,
    y1: Int,
    x2: Int,
    y2: Int,
) = buildList {
    (y1..y2).forEach { y ->
        (x1..x2).forEach { x ->
            add(x + y * 9)
        }
    }
}
