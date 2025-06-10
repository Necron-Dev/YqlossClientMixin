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

import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.yqloss.uktil.accessor.getValue
import net.yqloss.uktil.accessor.refs.triggerOnceUnary
import net.yqloss.uktil.event.EventRegistry
import net.yqloss.uktil.event.register
import net.yqloss.uktil.extension.floorInt
import net.yqloss.uktil.extension.type.equalTo
import net.yqloss.uktil.extension.type.notEqualTo
import net.yqloss.uktil.functional.plus
import net.yqloss.uktil.scope.longRet
import org.lwjgl.opengl.GL11.glScaled
import yqloss.yqlossclientmixinkt.api.internalLowerChestInventory
import yqloss.yqlossclientmixinkt.event.minecraft.YCMinecraftEvent
import yqloss.yqlossclientmixinkt.event.minecraft.YCRenderEvent
import yqloss.yqlossclientmixinkt.module.*
import yqloss.yqlossclientmixinkt.module.betterterminal.terminal.*
import yqloss.yqlossclientmixinkt.module.option.invoke
import yqloss.yqlossclientmixinkt.util.*
import kotlin.random.Random

val INFO_BETTER_TERMINAL = moduleInfo<BetterTerminalOptions>("better_terminal", "Better Terminal")

private val TERMINAL_FACTORIES =
    listOf(
        TerminalOrder,
        TerminalStart,
        TerminalColor,
        TerminalPanes,
        TerminalRubix,
        TerminalAlign,
    )

object BetterTerminal : YCModuleBase<BetterTerminalOptions>(INFO_BETTER_TERMINAL) {
    data class QueueData(
        val slotID: Int,
        val button: Int,
        val noWindowIDUpdate: Boolean,
    )

    class TerminalData private constructor(
        val terminal: Terminal,
        val enableQueue: Boolean,
        var windowID: Int,
        // states always have one more element than queue
        // the first element is the actual state in vanilla chest GUI
        // the last element is the prediction being rendered
        // queue[i] operation: states[i] -> states[i + 1]
        private val states: ArrayDeque<List<Int>>,
        private val queue: ArrayDeque<QueueData>,
        var clickDelay: Int,
    ) {
        constructor(
            terminal: Terminal,
            enableQueue: Boolean,
            windowID: Int,
            state: List<Int>,
            clickDelay: Int,
        ) : this(
            terminal,
            enableQueue,
            windowID,
            ArrayDeque(listOf(state)),
            ArrayDeque(),
            clickDelay,
        )

        val state get() = states[states.size - 1]

        fun add(
            state: List<Int>,
            queueData: QueueData,
        ) {
            states += state
            queue += queueData
        }

        fun pop() {
            states.removeFirst()
            queue.removeFirst()
        }

        val isQueueEmpty get() = queue.isEmpty()

        val actualState get() = states[0]

        val nextOperation get() = queue[0]

        val queueSize get() = queue.size
    }

    data class ParsedState(
        val chest: GuiChest,
        val inventory: IInventory,
        val windowID: Int,
        val title: String,
        val items: List<ItemStack?>,
        val terminal: Terminal,
        val state: List<Int>?,
    )

    val parsedState: ParsedState?
        get() {
            val chest = MC.currentScreen as? GuiChest ?: return null
            val title = chest.title.trimStyle
            val terminal = TERMINAL_FACTORIES.firstNotNullOfOrNull { it.createIfMatch(title) } ?: return null
            val inventory = chest.internalLowerChestInventory
            val items = (0..<inventory.sizeInventory).map(inventory::getStackInSlot)
            return ParsedState(
                chest,
                inventory,
                chest.inventorySlots.windowId,
                title,
                items,
                terminal,
                terminal.parse(items),
            )
        }

    var data: TerminalData? = null

    fun reloadTerminal() {
        data = null
    }

    fun switchToQueue(
        terminal: Terminal,
        windowID: Int,
        state: List<Int>,
    ) {
        this@BetterTerminal.data = TerminalData(
            terminal,
            options.enableQueue && terminal.enableQueue,
            windowID,
            state,
            -1,
        )
    }

    fun switchToNonQueue(
        terminal: Terminal,
        state: List<Int>,
    ) {
        this@BetterTerminal.data = TerminalData(
            terminal,
            false,
            -1,
            state,
            -1,
        )
    }

    fun switchLoading(terminal: Terminal) {
        this@BetterTerminal.data = TerminalData(
            TerminalLoading(terminal.lines, terminal.beginLine, terminal.chestLines, terminal.title),
            options.enableQueue && terminal.enableQueue,
            -1,
            listOf(),
            1,
        )
    }

    fun forceNonQueue() {
        val data = data ?: return
        switchToNonQueue(data.terminal, data.state)
    }

    private val randomDelay
        get(): Int {
            var from = options.clickDelayFrom
            val until = options.clickDelayUntil
            if (from < 0) from = 0.0
            return if (until <= from) from.floorInt else Random.nextDouble(from, until).floorInt
        }

    fun onQueueClick(
        data: TerminalData,
        slotID: Int,
        result: Terminal.Prediction,
    ) {
        val (click, noWindowIDUpdate) =
            when (result.clickType) {
                ClickType.CORRECT ->
                    true.also {
                        ClickType.CORRECT.notify(logger)
                    } to false

                ClickType.WRONG_WITH_WINDOW_ID_UPDATE ->
                    (!options.preventMisclick).also {
                        (if (it) ClickType.WRONG_WITH_WINDOW_ID_UPDATE else ClickType.CANCELED).notify(logger)
                    } to false

                ClickType.WRONG_WITHOUT_WINDOW_ID_UPDATE ->
                    (!options.preventMisclick).also {
                        (if (it) ClickType.WRONG_WITHOUT_WINDOW_ID_UPDATE else ClickType.CANCELED).notify(logger)
                    } to true

                ClickType.FAIL ->
                    (!options.preventMisclick && !options.preventFail).also {
                        (if (it) ClickType.FAIL else ClickType.CANCELED).notify(logger)
                    } to true

                else -> false to false
            }

        if (click) {
            if (data.isQueueEmpty) {
                data.clickDelay = randomDelay
            }
            data.add(
                result.state,
                QueueData(slotID, result.button, noWindowIDUpdate),
            )
        }
    }

    fun onNonQueueClick(
        slotID: Int,
        result: Terminal.Prediction,
    ) {
        when (result.clickType) {
            ClickType.WRONG_WITH_WINDOW_ID_UPDATE, ClickType.WRONG_WITHOUT_WINDOW_ID_UPDATE -> {
                if (options.preventMisclick) {
                    ClickType.CANCELED.notify(logger)
                    return
                }
            }

            ClickType.FAIL -> {
                if (options.preventMisclick || options.preventFail) {
                    ClickType.CANCELED.notify(logger)
                    return
                }
            }

            else -> {}
        }

        clickSlot(slotID, result.button)
        ClickType.NORMAL.notify(logger)
    }

    fun onClick(
        slotID: Int,
        button: Int,
    ) {
        val data = data ?: return
        val button = if (data.terminal.enableRightClick) button else 0
        val result = data.terminal.predict(data.state, slotID, button)
        if (result.clickType === ClickType.NONE) return
        if (data.enableQueue) {
            onQueueClick(data, slotID, result)
        } else {
            onNonQueueClick(slotID, result)
        }
    }

    private val onTick by triggerOnceUnary(::tickCounter) { (data, parsedState): Pair<TerminalData?, ParsedState> ->
        parsedState.tickAndPerformQueuedClicks(data)
    }

    // inplace operation on the parameter
    // will not do anything in non queue mode
    // will change this@BetterTerminal.data only if state mismatch
    private fun ParsedState.tickAndPerformQueuedClicks(data: TerminalData?) {
        val data = data ?: return
        // queue mode
        if (data.clickDelay >= 0) {
            // state mismatch
            // resets this@BetterTerminal.data
            if (state !== null && state notEqualTo data.actualState) {
                if (options.reloadOnMismatch) {
                    reloadTerminal()
                } else {
                    switchToNonQueue(terminal, state)
                }
                return
            }

            // time to click
            // does not change data.queue or data.states
            if (data.clickDelay == 0) {
                if (state === null) {
                    // the server has not updated the window
                    // check next tick
                    ++data.clickDelay
                } else if (!data.isQueueEmpty) {
                    val click = data.nextOperation
                    Screen.onHandleInput += {
                        clickSlot(click.slotID, click.button)
                    }
                    // if the click is wrong in some terminals, window id and state do not change
                    // so we need to skip the click, or it will freeze
                    // + 1 because --data.clickDelay later
                    if (click.noWindowIDUpdate) {
                        data.pop()
                        data.clickDelay = randomDelay + 1
                    }
                    // clickDelay goes to -1 later so it will not be clicked twice
                }
            }

            --data.clickDelay
        }
    }

    // update this@BetterTerminal.data by the parameter and this@ParsedState
    // will not modify the parameter
    private fun ParsedState.updateStoredData(data: TerminalData?) {
        if (data?.terminal == terminal) {
            if (state !== null) {
                if (!data.enableQueue) {
                    // update the state
                    switchToNonQueue(terminal, state)
                } else if (data.isQueueEmpty) {
                    // update the state
                    switchToQueue(terminal, windowID, state)
                } else {
                    val equalID = windowID == data.windowID
                    val equalState = state equalTo data.actualState

                    when {
                        // next click
                        !equalID && !equalState -> {
                            data.pop()
                            data.clickDelay = randomDelay
                            this@BetterTerminal.data = data
                        }

                        // got checked
                        equalID && !equalState -> switchToNonQueue(terminal, state)

                        // waiting for server to update the window
                        else -> this@BetterTerminal.data = data
                    }
                }
            } else {
                // wait for a complete window
                this@BetterTerminal.data = data
            }
        } else {
            if (state !== null) {
                switchToQueue(terminal, windowID, state)
            } else {
                // has not received a complete window
                switchLoading(terminal)
            }
        }
    }

    private fun updateChest(
        chest: GuiChest,
        data: TerminalData?,
    ) {
        parsedState?.run {
            Screen.setScreen(chest)
            updateStoredData(data)
            onTick { this@BetterTerminal.data to this }
        }
    }

    private fun clickSlot(
        slotID: Int,
        button: Int,
    ) {
        val chest = MC.currentScreen as? GuiChest ?: return
        MC.playerController.windowClick(
            chest.inventorySlots.windowId,
            slotID,
            if (button == 1) 1 else 2,
            if (button == 1) 0 else 3,
            MC.thePlayer,
        )
        options.onActualClick(logger) {
            this["slot"] = slotID
            this["button"] = button
        }
    }

    override val registerEvents: EventRegistry.() -> Unit
        get() = {
            super.registerEvents(this)

            register<YCRenderEvent.Screen.Proxy> { event ->
                val data = data
                Screen.proxiedScreen = null
                this@BetterTerminal.data = null

                enabled || longRet
                options.forceEnabled || inSkyBlock || longRet

                updateChest(event.screen as? GuiChest ?: longRet, data)
                this@BetterTerminal.data ?: longRet
                event.mutableScreen = Screen
            }

            register<BetterTerminalEvent.DrawDefaultBackground> { event ->
                !event.canceled && enabled || longRet
                event.canceled = event.screen === Screen.proxiedScreen
            }

            register<BetterTerminalEvent.RenderTooltip> { event ->
                !event.canceled && enabled || longRet
                event.canceled = event.screen === Screen.proxiedScreen
            }

            register<YCMinecraftEvent.Tick.Post> {
                Screen.onHandleInput = null
            }
        }

    object Screen : YCProxyScreen<GuiChest>() {
        override fun drawScreen(
            mouseX: Int,
            mouseY: Int,
            partialTicks: Float,
        ) {
            options.showChest || return
            glMatrixScope {
                glScaled(options.chestScale, options.chestScale, 1.0)
                proxiedScreen?.drawScreen(mouseX, mouseY, partialTicks)
            }
        }

        override fun handleInput() {
            val data = data
            proxiedScreen = null
            BetterTerminal.data = null
            updateChest(MC.currentScreen as? GuiChest ?: return, data)
            super.handleInput()
        }
    }
}
