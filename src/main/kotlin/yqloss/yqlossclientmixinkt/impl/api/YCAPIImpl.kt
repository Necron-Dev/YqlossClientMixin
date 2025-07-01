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

package yqloss.yqlossclientmixinkt.impl.api

import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.IInventory
import yqloss.yqlossclientmixinkt.api.YCAPI
import yqloss.yqlossclientmixinkt.api.YCHypixelLocation
import yqloss.yqlossclientmixinkt.impl.mixin.AccessorGuiChest
import yqloss.yqlossclientmixinkt.impl.mixin.AccessorGuiScreen
import yqloss.yqlossclientmixinkt.impl.option.language.LanguageManager
import yqloss.yqlossclientmixinkt.ycLogger

private val logger = ycLogger("API")

class YCAPIImpl : YCAPI {
    override var hypixelLocation: YCHypixelLocation? = null
    override val templateProvider = ::YCTemplateImpl

    override fun translate(string: String) = LanguageManager.translate(string)

    override fun call_GuiScreen_keyTyped(
        instance: GuiScreen,
        typedChar: Char,
        keyCode: Int,
    ) {
        (instance as AccessorGuiScreen).keyTyped_yc(typedChar, keyCode)
    }

    override fun get_GuiChest_lowerChestInventory(instance: GuiChest): IInventory {
        return (instance as AccessorGuiChest).lowerChestInventory_yc
    }
}
