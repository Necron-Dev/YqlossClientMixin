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

package yqloss.yqlossclientmixinkt.module.ycleapmenu

import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.init.Items
import yqloss.yqlossclientmixinkt.YC
import yqloss.yqlossclientmixinkt.event.YCEventRegistry
import yqloss.yqlossclientmixinkt.event.minecraft.YCMinecraftEvent
import yqloss.yqlossclientmixinkt.event.minecraft.YCRenderEvent
import yqloss.yqlossclientmixinkt.event.register
import yqloss.yqlossclientmixinkt.module.*
import yqloss.yqlossclientmixinkt.module.betterterminal.BetterTerminal
import yqloss.yqlossclientmixinkt.module.option.invoke
import yqloss.yqlossclientmixinkt.util.MC
import yqloss.yqlossclientmixinkt.util.accessor.getValue
import yqloss.yqlossclientmixinkt.util.accessor.refs.trigger
import yqloss.yqlossclientmixinkt.util.functional.plus
import yqloss.yqlossclientmixinkt.util.scope.longRun
import yqloss.yqlossclientmixinkt.util.tickCounter
import yqloss.yqlossclientmixinkt.util.trimStyle

val INFO_YC_LEAP_MENU = moduleInfo<YCLeapMenuOptions>("yc_leap_menu", "YC Leap Menu")

private val REGEX_IN_BRACKETS = Regex("\\[.*]")
private val REGEX_TAB_NAME = Regex("([A-Za-z0-9_]{1,16})\\s*\\((Archer|Berserk|Mage|Healer|Tank) [A-Z]+\\)")
private val REGEX_NAME = Regex("[A-Za-z0-9_]{1,16}")

object YCLeapMenu : YCModuleBase<YCLeapMenuOptions>(INFO_YC_LEAP_MENU) {
    private var onHandleInput: (() -> Unit)? = null

    data class PlayerInfo(
        val profile: NetworkPlayerInfo,
        val theClass: CatacombsClass,
        val dead: Boolean,
    )

    private val playerNetworkMap = mutableMapOf<String, NetworkPlayerInfo>()

    private val playerClassMap = mutableMapOf<String, CatacombsClass>()

    private val playerDeadMap = mutableMapOf<String, Boolean>()

    private var leapOrder = listOf<String?>(null, null, null, null, null)

    private val preferredLeap: String? = null

    private fun getPlayerInfo(name: String): PlayerInfo? {
        if (name !in playerNetworkMap || name !in playerClassMap) {
            return null
        }
        return PlayerInfo(playerNetworkMap[name]!!, playerClassMap[name]!!, playerDeadMap[name] ?: false)
    }

    fun clearDeadMap() {
        playerDeadMap.clear()
    }

    fun getPlayerInfo(index: Int): PlayerInfo? {
        return (if (index == -1) preferredLeap else leapOrder[index])?.let(::getPlayerInfo)
    }

    private val loadLeapInfo by trigger(::tickCounter) {
        val chest = MC.currentScreen as? GuiChest ?: return@trigger
        val inventory = YC.api.get_GuiChest_lowerChestInventory(chest)

        MC.thePlayer.sendQueue.playerInfoMap
            .forEach { info ->
                if (info.gameProfile.name in playerClassMap) {
                    playerNetworkMap[info.gameProfile.name] = info
                    return@forEach
                }
                REGEX_TAB_NAME
                    .matchEntire(
                        MC.ingameGUI.tabList
                            .getPlayerName(info)
                            .trimStyle
                            .filter { it.code in 32..126 }
                            .replace(REGEX_IN_BRACKETS, "")
                            .trim(),
                    )?.let { result ->
                        val className = result.groupValues[2]
                        playerClassMap[result.groupValues[1]] =
                            CatacombsClass.entries.firstOrNull { it.displayName == className }
                                ?: return@forEach
                    }
            }

        val playerSet = mutableSetOf<String>()

        (9..17).forEach { slotID ->
            if (slotID >= inventory.sizeInventory) return@forEach
            val itemStack = inventory.getStackInSlot(slotID) ?: return@forEach
            if (itemStack.item !== Items.skull) return@forEach
            var name = itemStack.displayName.trimStyle
            if (' ' in name) {
                name = name.split(' ').run { get(size - 1) }
            }
            if (!name.matches(REGEX_NAME)) return@forEach
            playerSet.add(name)
            playerDeadMap[name] =
                itemStack.getTooltip(MC.thePlayer, false).all { it.trimStyle != "Click to teleport!" }
        }

        val playerList =
            playerSet
                .toList()
                .sorted()
                .map { it to (playerClassMap[it] ?: CatacombsClass.UNKNOWN) }
                .toMutableList()

        fun takeClass(theClass: CatacombsClass): Pair<String, CatacombsClass>? {
            return playerList.firstOrNull { it.second === theClass }?.also(playerList::remove)
        }

        leapOrder =
            listOf(
                takeClass(CatacombsClass.ARCHER),
                takeClass(CatacombsClass.BERSERK),
                takeClass(CatacombsClass.MAGE),
                takeClass(CatacombsClass.HEALER),
                takeClass(CatacombsClass.TANK),
            ).map { (it ?: playerList.removeFirstOrNull())?.first }
    }

    fun leapTo(target: String) {
        if (MC.currentScreen !is GuiChest) return
        Screen.onHandleInput += handler@{
            longRun {
                ensureEnabled()
                ensureInWorld()
                if (!options.forceEnabled) {
                    ensureSkyBlockMode("dungeon")
                }
                val chest = MC.currentScreen as? GuiChest ?: return@handler
                ensureWindowTitles(chest, setOf("Spirit Leap", "Teleport to Player"))
                val inventory = YC.api.get_GuiChest_lowerChestInventory(chest)
                (9..17).firstOrNull {
                    var name = inventory.getStackInSlot(it)?.displayName?.trimStyle ?: ""
                    if (' ' in name) {
                        name = name.split(' ').run { get(size - 1) }
                    }
                    if (it < inventory.sizeInventory && target == name) {
                        MC.playerController.windowClick(
                            chest.inventorySlots.windowId,
                            it,
                            2,
                            3,
                            MC.thePlayer,
                        )
                        options.onClickLeap(logger) {
                            this["name"] = name
                            this["class"] = playerClassMap[name] ?: CatacombsClass.UNKNOWN
                        }
                        true
                    } else {
                        false
                    }
                }
            }
            Unit
        }
    }

    override fun registerEvents(registry: YCEventRegistry) {
        registry.run {
            register<YCRenderEvent.Screen.Proxy> { event ->
                longRun {
                    Screen.proxiedScreen = null

                    ensureEnabled()
                    ensureInWorld()

                    if (!options.forceEnabled) {
                        ensureSkyBlockMode("dungeon")
                    }

                    val chest = event.screen as? GuiChest ?: return@register

                    ensureWindowTitles(chest, setOf("Spirit Leap", "Teleport to Player"))

                    loadLeapInfo

                    Screen.setScreen(chest)
                    event.mutableScreen = Screen
                }
            }

            register<YCMinecraftEvent.LoadWorld.Pre> {
                playerNetworkMap.clear()
                playerClassMap.clear()
                playerDeadMap.clear()
            }

            register<YCMinecraftEvent.Tick.Post> {
                BetterTerminal.Screen.onHandleInput = null
            }
        }
    }

    object Screen : YCProxyScreen<GuiChest>()
}
