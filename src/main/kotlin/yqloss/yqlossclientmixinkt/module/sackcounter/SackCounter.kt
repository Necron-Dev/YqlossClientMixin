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

package yqloss.yqlossclientmixinkt.module.sackcounter

import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.event.HoverEvent
import net.yqloss.uktil.event.EventRegistry
import net.yqloss.uktil.event.register
import net.yqloss.uktil.scope.longRet
import yqloss.yqlossclientmixinkt.api.internalLowerChestInventory
import yqloss.yqlossclientmixinkt.event.minecraft.YCMinecraftEvent
import yqloss.yqlossclientmixinkt.event.minecraft.YCPacketEvent
import yqloss.yqlossclientmixinkt.module.*
import yqloss.yqlossclientmixinkt.util.MC
import yqloss.yqlossclientmixinkt.util.trimStyle

val INFO_SACK_COUNTER = moduleInfo<SackCounterOptions>("sack_counter", "Sack Counter")

private val REGEX_SACK_WINDOW = Regex("^.*Sack.*$")
private val REGEX_STORED_LORE = Regex("^Stored: ([0-9,]+)/.*$")
private val REGEX_SACKS_MESSAGE = Regex("^\\[Sacks] .*?item.*? \\(Last .*?\\)$")
private val REGEX_SACKS_INCREMENT = Regex("^\\+([0-9,]+) ([A-Za-z0-9 \\-]+?) \\(.*\\)$")

object SackCounter : YCModuleBase<SackCounterOptions>(INFO_SACK_COUNTER) {
    private val nameToCountMap = mutableMapOf<String, Int>()

    fun getCount(name: String) = nameToCountMap[name]

    val available get() = enabled && inWorld && inSkyBlock

    private fun ensure(screen: GuiScreen?): GuiChest {
        available || longRet

        val chest = screen as? GuiChest ?: longRet
        isWindowTitled(chest, REGEX_SACK_WINDOW) || longRet

        return chest
    }

    override val registerEvents: EventRegistry.() -> Unit
        get() = {
            super.registerEvents(this)

            register<YCMinecraftEvent.Tick.Pre> {
                val chest = ensure(MC.currentScreen)
                val inventory = chest.internalLowerChestInventory

                repeat(inventory.sizeInventory) { i ->
                    val itemStack = inventory.getStackInSlot(i) ?: return@repeat
                    val name = itemStack.displayName.trimStyle

                    itemStack.getTooltip(MC.thePlayer, false).forEach { line ->
                        REGEX_STORED_LORE.matchEntire(line.trimStyle)?.let { result ->
                            val count = result.groupValues[1].replace(",", "").toIntOrNull() ?: return@forEach
                            nameToCountMap[name] = count
                        }
                    }
                }
            }

            register<YCPacketEvent.S02.Chat.Pre> { event ->
                val message = event.component.formattedText.trimStyle
                REGEX_SACKS_MESSAGE.matches(message) || longRet

                event.component.siblings.forEach { sibling ->
                    val hoverEvent = sibling.chatStyle.chatHoverEvent ?: return@forEach
                    hoverEvent.action === HoverEvent.Action.SHOW_TEXT || return@forEach
                    val lines = hoverEvent.value?.formattedText?.trimStyle?.split('\n') ?: return@forEach
                    var anyIncrement = false
                    lines.forEach { line ->
                        REGEX_SACKS_INCREMENT.matchEntire(line.trimStyle)?.let { result ->
                            val count = result.groupValues[1].replace(",", "").toIntOrNull() ?: return@forEach
                            val name = result.groupValues[2]
                            nameToCountMap.computeIfPresent(name) { _, last -> last + count }
                            anyIncrement = true
                        }
                    }
                    if (anyIncrement) return@register
                }
            }
        }
}
