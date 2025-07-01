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

package yqloss.yqlossclientmixinkt.api

import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.IInventory
import yqloss.yqlossclientmixinkt.YC

@Suppress("ktlint:standard:function-naming")
interface YCAPI {
    val hypixelLocation: YCHypixelLocation?
    val templateProvider: YCTemplateProvider

    fun translate(string: String): String

    fun call_GuiScreen_keyTyped(
        instance: GuiScreen,
        typedChar: Char,
        keyCode: Int,
    )

    fun get_GuiChest_lowerChestInventory(instance: GuiChest): IInventory
}

fun GuiScreen.internalKeyTyped(
    typedChar: Char,
    keyCode: Int,
) = YC.api.call_GuiScreen_keyTyped(this, typedChar, keyCode)

val GuiChest.internalLowerChestInventory get() = YC.api.get_GuiChest_lowerChestInventory(this)

inline fun YCAPI.format(
    template: String,
    placeholder: YCTemplate.() -> Unit = {},
) = templateProvider(template).also(placeholder).format()

inline fun YCAPI.formatTranslated(
    template: String,
    placeholder: YCTemplate.() -> Unit = {},
) = templateProvider(translate(template)).also(placeholder).format()

inline fun formatTranslated(
    template: String,
    placeholder: YCTemplate.() -> Unit = {},
) = YC.api.formatTranslated(template) {
    setDefault()
    placeholder()
}
