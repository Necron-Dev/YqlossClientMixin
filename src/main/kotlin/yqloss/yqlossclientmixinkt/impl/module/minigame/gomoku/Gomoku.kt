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

package yqloss.yqlossclientmixinkt.impl.module.minigame.gomoku

import kotlinx.serialization.json.*
import net.minecraft.client.gui.GuiScreen
import net.yqloss.uktil.event.EventRegistry
import net.yqloss.uktil.event.register
import net.yqloss.uktil.extension.byte
import net.yqloss.uktil.extension.int
import net.yqloss.uktil.extension.sameNotNull
import net.yqloss.uktil.generic.castTo
import net.yqloss.uktil.math.Vec2D
import net.yqloss.uktil.math.asFloorVec2I
import net.yqloss.uktil.scope.longRet
import net.yqloss.uktil.scope.noExcept
import yqloss.yqlossclientmixinkt.event.minecraft.YCCommandEvent
import yqloss.yqlossclientmixinkt.event.minecraft.YCInputEvent
import yqloss.yqlossclientmixinkt.event.minecraft.YCMinecraftEvent
import yqloss.yqlossclientmixinkt.event.minecraft.YCRenderEvent
import yqloss.yqlossclientmixinkt.impl.module.YCModuleScreenBase
import yqloss.yqlossclientmixinkt.impl.module.minigame.*
import yqloss.yqlossclientmixinkt.impl.module.minigame.MultiplayerMinigame.MatchMake
import yqloss.yqlossclientmixinkt.impl.nanovgui.Transformation
import yqloss.yqlossclientmixinkt.impl.nanovgui.Widget
import yqloss.yqlossclientmixinkt.impl.nanovgui.widget.EllipseWidget
import yqloss.yqlossclientmixinkt.impl.nanovgui.widget.RoundedRectWidget
import yqloss.yqlossclientmixinkt.module.NO_MODULE_INFO
import yqloss.yqlossclientmixinkt.module.YCModule
import yqloss.yqlossclientmixinkt.module.YCProxyScreen
import yqloss.yqlossclientmixinkt.module.channelmanager.ChannelManagerEvent
import yqloss.yqlossclientmixinkt.module.option.YCModuleOptions
import yqloss.yqlossclientmixinkt.util.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.random.Random

private const val GRID_SIZE = 12.0

private const val DOT_SIZE = 2.0

private const val PIECE_SIZE = 5.0

object Gomoku :
    YCModuleScreenBase<YCModuleOptions, YCModule<YCModuleOptions>>(NO_MODULE_INFO),
    MultiplayerMinigame<Gomoku.Data> {
    data class Data(
        val myTurn: Boolean,
        val color: Boolean,
        val board: List<List<Gomoku.BoardPointState>> = List(15) { List(15) { BoardPointState.UNOCCUPIED } },
    )

    override val channel = ":gomoku"

    override val minigameState = MultiplayerMinigame.State()

    override val width = 0.0

    override val height = 0.0

    override val ensureShow get() = ProxyScreen.proxiedScreen sameNotNull MC.currentScreen

    private var firstTurnMode = FirstTurnMode.RANDOM

    override fun displayMatchMake(event: ChannelManagerEvent.Message, matchMake: MatchMake) {
        printChat {
            -"Match: ${matchMake.currentPlayers} / ${matchMake.neededPlayers}"
        }
    }

    override fun onGameEnd(reason: String, data: JsonElement, sender: String?) {
        printChat {
            -reason
        }
    }

    override fun getInitialState() = Data(
        when (firstTurnMode) {
            FirstTurnMode.SELF -> true
            FirstTurnMode.OPPONENT -> false
            else -> Random.nextBoolean()
        },
        false,
    )

    override fun encodeInitialState(state: Data) = if (state.myTurn) JsonPrimitive(true) else JsonPrimitive(false)

    override fun decodeInitialState(json: JsonElement) = Data(!json.jsonPrimitive.boolean, false)

    @OptIn(ExperimentalEncodingApi::class)
    override fun hashState(state: Data) = Base64.encode(
        md5(
            ByteArray(256).also {
                repeat(15) { ix ->
                    repeat(15) { iy ->
                        it[ix * 15 + iy] = state.board[ix][iy].ordinal.byte
                    }
                }
                it[225] = if (state.myTurn) 1 else 0
            },
        ),
    ).substring(0..<8)

    override fun hashStateForCheck(state: Data) = hashState(state.copy(myTurn = !state.myTurn))

    override fun execute(state: Data, operation: JsonElement, sender: String?): Data? = noExcept({
        endGame("Error")
        null
    }) {
        val canPlace = sender == null == state.myTurn

        val x = operation.jsonArray[0].jsonPrimitive.int
        val y = operation.jsonArray[1].jsonPrimitive.int

        if (!canPlace || state.board[x][y] != BoardPointState.UNOCCUPIED) {
            endGame("Illegal move")
            return null
        }

        val copiedBoard = List(15) { state.board[it].toMutableList() }

        copiedBoard[x][y] = if (state.color) BoardPointState.WHITE else BoardPointState.BLACK

        return Data(
            myTurn = !state.myTurn,
            color = !state.color,
            board = copiedBoard,
        )
    }

    override fun postExecute(state: Data, newState: Data?, operation: JsonElement, sender: String?) {
        newState ?: return

        val x = operation.jsonArray[0].jsonPrimitive.int
        val y = operation.jsonArray[1].jsonPrimitive.int
        val piece = newState.board[x][y]

        fun getConnected(dx: Int, dy: Int): Int {
            var count = 1
            repeat(4) {
                val px = x + dx * count
                val py = y + dy * count
                px in 0..14 && py in 0..14 && newState.board[px][py] == piece || return count
                ++count
            }
            return count
        }

        val win = getConnected(1, 0) + getConnected(-1, 0) >= 6 ||
            getConnected(0, 1) + getConnected(0, -1) >= 6 ||
            getConnected(1, 1) + getConnected(-1, -1) >= 6 ||
            getConnected(-1, 1) + getConnected(1, -1) >= 6

        if (win) {
            endGame("${if (state.color) "White" else "Black"} wins!")
        }
    }

    private fun drawBoard(widgets: MutableList<Widget<*>>, tr: Transformation) {
        widgets += RoundedRectWidget(
            tr pos Vec2D(-110.0, -110.0),
            tr pos Vec2D(110.0, 110.0),
            0xFFF0B060.int,
            tr size 8.0,
        )

        repeat(15) {
            widgets += RoundedRectWidget(
                tr pos Vec2D(x = -7 * GRID_SIZE, y = (it - 7) * GRID_SIZE),
                (tr pos Vec2D(7 * GRID_SIZE, (it - 7) * GRID_SIZE)) + Vec2D(1.0, 1.0),
                0xFF000000.int,
                tr size 0.0,
            )

            widgets += RoundedRectWidget(
                tr pos Vec2D((it - 7) * GRID_SIZE, -7 * GRID_SIZE),
                (tr pos Vec2D((it - 7) * GRID_SIZE, 7 * GRID_SIZE)) + Vec2D(1.0, 1.0),
                0xFF000000.int,
                tr size 0.0,
            )
        }

        fun addDot(x: Int, y: Int) {
            widgets += EllipseWidget(
                tr pos Vec2D((x - 7) * GRID_SIZE - DOT_SIZE, (y - 7) * GRID_SIZE - DOT_SIZE),
                (tr pos Vec2D((x - 7) * GRID_SIZE + DOT_SIZE, (y - 7) * GRID_SIZE + DOT_SIZE)) + Vec2D(1.0, 1.0),
                0xFF000000.int,
            )
        }

        addDot(7, 7)
        addDot(3, 3)
        addDot(3, 11)
        addDot(11, 3)
        addDot(11, 11)
    }

    private fun drawPieces(
        widgets: MutableList<Widget<*>>,
        tr: Transformation,
        state: MultiplayerMinigame.State.InGame<Data>,
    ) {
        repeat(15) { ix ->
            val line = state.data.board[ix]

            repeat(15) inner@{ iy ->
                val color = when (line[iy]) {
                    BoardPointState.BLACK -> 0xFF000000.int
                    BoardPointState.WHITE -> 0xFFFFFFFF.int
                    else -> return@inner
                }

                widgets += EllipseWidget(
                    tr pos Vec2D((ix - 7) * GRID_SIZE - PIECE_SIZE, (iy - 7) * GRID_SIZE - PIECE_SIZE),
                    (tr pos Vec2D((ix - 7) * GRID_SIZE + PIECE_SIZE, (iy - 7) * GRID_SIZE + PIECE_SIZE)) + Vec2D(
                        1.0,
                        1.0,
                    ),
                    color,
                )
            }
        }
    }

    private fun drawInGame(
        widgets: MutableList<Widget<*>>,
        tr: Transformation,
        state: MultiplayerMinigame.State.InGame<Data>,
    ) {
        drawBoard(widgets, tr)
        drawPieces(widgets, tr, state)
    }

    override fun draw(widgets: MutableList<Widget<*>>, box: Vec2D, tr: Transformation) {
        val data: MultiplayerMinigame.State.InGame<*> = when (val state = minigameState.state) {
            is MultiplayerMinigame.State.InGame<*> -> state
            is MultiplayerMinigame.State.Ended<*> -> state.state
            else -> return
        }
        drawInGame(widgets, tr, data.castTo())
    }

    private var displayScreen = false

    override val registerEvents: EventRegistry.() -> Unit
        get() = {
            super.registerEvents(this)
            super.registerMinigameEvents(this)

            register<YCInputEvent.Mouse.Click> { event ->
                event.screen || longRet

                val tr = transformation

                val mouse = !tr pos mousePosition

                (minigameState.state as? MultiplayerMinigame.State.InGame<*>).castTo<MultiplayerMinigame.State.InGame<Data>?>()
                    ?.let { state ->
                        state.data.myTurn || return@let
                        val (x, y) = (mouse / GRID_SIZE + Vec2D(7.5, 7.5)).asFloorVec2I
                        if (x in 0..14 && y in 0..14 && state.data.board[x][y] == BoardPointState.UNOCCUPIED) {
                            operate(JsonArray(listOf(JsonPrimitive(x), JsonPrimitive(y))))
                        }
                    }
            }

            register<YCRenderEvent.Screen.Proxy> { event ->
                event.screen === MCScreen || longRet
                ProxyScreen.setScreen(MCScreen)
                event.mutableScreen = ProxyScreen
            }

            register<YCMinecraftEvent.Tick.Pre> {
                if (displayScreen) {
                    displayScreen = false
                    MC.displayGuiScreen(MCScreen)
                }
            }

            register<YCCommandEvent.Execute> { event ->
                !event.canceled && !event.disableClientCommand || longRet

                noExcept(::printError) {
                    when (event.args.getOrNull(0)) {
                        "/ycg", "/yqlossclientgame" -> when (event.args.getOrNull(1)) {
                            "gomoku" -> {
                                event.canceled = true
                                displayScreen = true
                            }

                            "joingomoku" -> {
                                event.canceled = true
                                requestJoin(event.args.getOrNull(2) ?: longRet)
                            }

                            "newgomoku" -> {
                                event.canceled = true
                                createGame(
                                    MultiplayerMinigame.State.Hosting(
                                        freeSlots = 1,
                                        neededPlayers = 1,
                                    ),
                                )
                            }
                        }
                    }
                }
            }
        }

    enum class BoardPointState {
        UNOCCUPIED,
        BLACK,
        WHITE,
    }

    enum class FirstTurnMode {
        SELF,
        OPPONENT,
        RANDOM,
    }

    object MCScreen : GuiScreen() {
        override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
            drawDefaultBackground()
        }
    }

    object ProxyScreen : YCProxyScreen<GuiScreen>()
}
