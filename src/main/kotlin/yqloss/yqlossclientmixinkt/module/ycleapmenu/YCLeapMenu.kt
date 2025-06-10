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

import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.init.Items
import net.yqloss.uktil.accessor.getValue
import net.yqloss.uktil.accessor.refs.trigger
import net.yqloss.uktil.event.EventRegistry
import net.yqloss.uktil.event.register
import net.yqloss.uktil.functional.plus
import net.yqloss.uktil.scope.longRet
import yqloss.yqlossclientmixinkt.api.internalLowerChestInventory
import yqloss.yqlossclientmixinkt.api.setDefault
import yqloss.yqlossclientmixinkt.event.minecraft.YCMinecraftEvent
import yqloss.yqlossclientmixinkt.event.minecraft.YCRenderEvent
import yqloss.yqlossclientmixinkt.module.*
import yqloss.yqlossclientmixinkt.module.betterterminal.BetterTerminal
import yqloss.yqlossclientmixinkt.module.option.invoke
import yqloss.yqlossclientmixinkt.util.MC
import yqloss.yqlossclientmixinkt.util.middleClickChest
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

    private val playerNetworkInfoMap = mutableMapOf<String, NetworkPlayerInfo>()

    private val playerClassMap = mutableMapOf<String, CatacombsClass>()

    private val playerDeadMap = mutableMapOf<String, Boolean>()

    private var leapOrder = listOf<String?>(null, null, null, null, null)

    private val preferredLeap: String? = null

    fun clearDeadMap() {
        playerDeadMap.clear()
    }

    private fun getPlayerInfo(name: String): PlayerInfo? {
        return PlayerInfo(
            playerNetworkInfoMap[name] ?: return null,
            playerClassMap[name] ?: return null,
            playerDeadMap[name] ?: false,
        )
    }

    fun getPlayerInfo(index: Int): PlayerInfo? {
        return (if (index == -1) preferredLeap else leapOrder[index])?.let(::getPlayerInfo)
    }

    private fun ensure(screen: GuiScreen?): GuiChest? {
        enabled && inWorld || return null
        options.forceEnabled || inSkyblockMode("dungeon") || return null

        val chest = screen as? GuiChest ?: longRet
        isWindowTitled(chest, setOf("Spirit Leap", "Teleport to Player")) || return null

        return chest
    }

    private val loadLeapInfo by trigger(::tickCounter) {
        val inventory = (MC.currentScreen as? GuiChest ?: return@trigger).internalLowerChestInventory

        MC.thePlayer.sendQueue.playerInfoMap.forEach { info ->
            if (info.gameProfile.name in playerClassMap) {
                playerNetworkInfoMap[info.gameProfile.name] = info
                return@forEach
            }
            REGEX_TAB_NAME.matchEntire(
                MC.ingameGUI.tabList
                    .getPlayerName(info)
                    .trimStyle
                    .filter { it.code in 32..126 }
                    .replace(REGEX_IN_BRACKETS, "")
                    .trim(),
            )?.let { result ->
                val className = result.groupValues[2]
                playerClassMap[result.groupValues[1]] =
                    CatacombsClass.entries.firstOrNull { it.displayName == className } ?: return@forEach
            }
        }

        val playerSet = mutableSetOf<String>()

        (9..17).forEach { slotID ->
            slotID < inventory.sizeInventory || return@forEach
            val itemStack = inventory.getStackInSlot(slotID) ?: return@forEach
            itemStack.item === Items.skull || return@forEach
            var name = itemStack.displayName.trimStyle
            if (' ' in name) name = name.split(' ').run { get(size - 1) }
            name.matches(REGEX_NAME) || return@forEach
            playerSet += name
            playerDeadMap[name] = itemStack.getTooltip(MC.thePlayer, false).all { it.trimStyle != "Click to teleport!" }
        }

        val playerList = playerSet
            .toList()
            .sorted()
            .map { it to (playerClassMap[it] ?: CatacombsClass.UNKNOWN) }
            .toMutableList()

        fun takeClass(theClass: CatacombsClass): Pair<String, CatacombsClass>? {
            return playerList.firstOrNull { it.second === theClass }?.also(playerList::remove)
        }

        leapOrder = listOf(
            takeClass(CatacombsClass.ARCHER),
            takeClass(CatacombsClass.BERSERK),
            takeClass(CatacombsClass.MAGE),
            takeClass(CatacombsClass.HEALER),
            takeClass(CatacombsClass.TANK),
        ).map { (it ?: playerList.removeFirstOrNull())?.first }
    }

    fun leapTo(target: String) {
        ensure(MC.currentScreen) ?: return

        Screen.onHandleInput += handler@{
            val inventory = (ensure(MC.currentScreen) ?: longRet).internalLowerChestInventory
            (9..17).firstOrNull {
                var name = inventory.getStackInSlot(it)?.displayName?.trimStyle ?: ""
                if (' ' in name) name = name.split(' ').run { get(size - 1) }
                if (it < inventory.sizeInventory && target == name) {
                    middleClickChest(it)
                    options.onClickLeap(logger) {
                        setDefault()
                        this["name"] = name
                        this["class"] = playerClassMap[name] ?: CatacombsClass.UNKNOWN
                    }
                    true
                } else {
                    false
                }
            }
            Unit
        }
    }

    override val registerEvents: EventRegistry.() -> Unit = {
        super.registerEvents(this)

        register<YCRenderEvent.Screen.Proxy> { event ->
            Screen.proxiedScreen = null

            val chest = ensure(event.screen) ?: longRet

            loadLeapInfo

            Screen.setScreen(chest)
            event.mutableScreen = Screen
        }

        register<YCMinecraftEvent.LoadWorld.Pre> {
            playerNetworkInfoMap.clear()
            playerClassMap.clear()
            playerDeadMap.clear()
        }

        register<YCMinecraftEvent.Tick.Post> {
            BetterTerminal.Screen.onHandleInput = null
        }
    }

    object Screen : YCProxyScreen<GuiChest>()

    init {
        register
    }
}
