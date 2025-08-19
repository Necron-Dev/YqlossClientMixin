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

package yqloss.yqlossclientmixinkt.module

import net.minecraft.client.gui.inventory.GuiChest
import yqloss.yqlossclientmixinkt.YC
import yqloss.yqlossclientmixinkt.api.internalLowerChestInventory
import yqloss.yqlossclientmixinkt.util.MC
import yqloss.yqlossclientmixinkt.util.trimStyle

val inWorld get() = MC.theWorld !== null

val inGUI get() = MC.currentScreen !== null

val inSkyBlock get() = MC.theWorld !== null && YC.api.hypixelLocation?.serverType?.name == "SkyBlock"

fun inSkyblockMode(mode: String) = inSkyBlock && YC.api.hypixelLocation?.mode == mode

fun inSkyblockMode(modes: Collection<String>) = inSkyBlock && YC.api.hypixelLocation?.mode in modes

fun isWindowTitled(
    chest: GuiChest,
    title: String,
) = chest.internalLowerChestInventory.name.trimStyle == title

fun isWindowTitled(
    chest: GuiChest,
    title: Regex,
) = title.matches(chest.internalLowerChestInventory.name.trimStyle)

fun isWindowTitled(
    chest: GuiChest,
    titles: Collection<String>,
) = chest.internalLowerChestInventory.name.trimStyle in titles

val SKYBLOCK_MINING_ISLANDS = setOf(
    "mining_1",
    "mining_2",
    "mining_3",
    "crystal_hollows",
    "mineshaft",
    "combat_3",
    "crimson_isle",
)
