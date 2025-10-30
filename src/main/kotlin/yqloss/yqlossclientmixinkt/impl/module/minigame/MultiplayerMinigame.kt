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

package yqloss.yqlossclientmixinkt.impl.module.minigame

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import net.yqloss.uktil.event.EventRegistry
import net.yqloss.uktil.event.register
import net.yqloss.uktil.generic.castTo
import net.yqloss.uktil.scope.longRet
import yqloss.yqlossclientmixinkt.YCJson
import yqloss.yqlossclientmixinkt.impl.module.minigame.MultiplayerMinigame.State
import yqloss.yqlossclientmixinkt.module.channelmanager.ChannelManager
import yqloss.yqlossclientmixinkt.module.channelmanager.ChannelManagerEvent
import yqloss.yqlossclientmixinkt.util.TextBuilderContext.Companion.green
import yqloss.yqlossclientmixinkt.util.TextBuilderContext.Companion.invoke
import yqloss.yqlossclientmixinkt.util.TextBuilderContext.Companion.red
import yqloss.yqlossclientmixinkt.util.printChat

interface MultiplayerMinigame<T> {
    val channel: String

    val minigameState: State

    val registerMinigameEvents: EventRegistry.() -> Unit
        get() = {
            register<ChannelManagerEvent.Message> { event ->
                !event.canceled && event.channel == channel && event.sender != event.self || longRet
                when (event.message.jsonObject["i"]?.jsonPrimitive?.content) {
                    "m" -> YCJson.decodeFromJsonElement<MatchMake>(event.message)
                    "a" -> YCJson.decodeFromJsonElement<Accept>(event.message)
                    "d" -> YCJson.decodeFromJsonElement<Deny>(event.message)
                    "b" -> YCJson.decodeFromJsonElement<Begin>(event.message)
                    "e" -> YCJson.decodeFromJsonElement<End>(event.message)
                    "x" -> YCJson.decodeFromJsonElement<Execute>(event.message)
                    else -> longRet
                }.process(event, this@MultiplayerMinigame)
            }
        }

    data class State(
        val matches: MutableMap<String, MatchInfo> = mutableMapOf(),
        var state: MatchState? = null,
    ) {
        data class MatchInfo(
            val currentPlayers: Int,
            val neededPlayers: Int,
            val data: JsonElement,
        )

        sealed interface MatchState

        class Requested(val name: String) : MatchState

        class Accepted(val name: String) : MatchState

        class Hosting(
            val joined: MutableSet<String> = mutableSetOf(),
            val invitations: MutableSet<String> = mutableSetOf(),
            var freeSlots: Int = 0,
            val neededPlayers: Int = 0,
        ) : MatchState

        data class InGame<T>(
            val name: String,
            val names: Set<String>,
            val data: T,
        ) : MatchState

        class Ended<T>(val state: InGame<T>) : MatchState
    }

    private interface Packet {
        fun <T> process(event: ChannelManagerEvent.Message, minigame: MultiplayerMinigame<T>)
    }

    fun displayMatchMake(event: ChannelManagerEvent.Message, matchMake: MatchMake)

    fun onGameEnd(reason: String, data: JsonElement, sender: String?)

    fun getInitialState(): T

    fun encodeInitialState(state: T): JsonElement

    fun decodeInitialState(json: JsonElement): T

    fun hashState(state: T): String

    fun hashStateForCheck(state: T): String

    fun execute(state: T, operation: JsonElement, sender: String?): T?

    fun postExecute(state: T, newState: T?, operation: JsonElement, sender: String?)

    @Serializable
    data class MatchMake(
        @SerialName("i") val id: String = "m",
        @SerialName("n") val invitations: List<String>,
        @SerialName("f") val freeSlots: Int,
        @SerialName("c") val currentPlayers: Int,
        @SerialName("t") val neededPlayers: Int,
        @SerialName("d") val data: JsonElement = JsonNull,
    ) : Packet {
        override fun <T> process(event: ChannelManagerEvent.Message, minigame: MultiplayerMinigame<T>) {
            val canJoin = freeSlots != 0 || event.self in invitations
            if (canJoin) {
                minigame.minigameState.matches[event.sender] = State.MatchInfo(currentPlayers, neededPlayers, data)
                minigame.displayMatchMake(event, this)
            } else {
                minigame.minigameState.matches.remove(event.sender)?.let {
                    printChat { -red("You can no longer join ${event.sender}'s game!") }
                }
            }
        }
    }

    @Serializable
    data class Accept(
        @SerialName("i") val id: String = "a",
        @SerialName("n") val name: String,
        @SerialName("d") val data: JsonElement = JsonNull,
    ) : Packet {
        override fun <T> process(event: ChannelManagerEvent.Message, minigame: MultiplayerMinigame<T>) {
            name == event.self || return

            when (val state = minigame.minigameState.state) {
                is State.Requested -> {
                    state.name == event.sender || return
                    minigame.minigameState.state = State.Accepted(state.name)
                }

                is State.Hosting -> {
                    if (event.sender in state.invitations) {
                        state.invitations -= event.sender
                        state.joined += event.sender
                        minigame.send(Accept(name = event.sender))
                        printChat { -green("Invited player ${event.sender} joined! (${state.joined.size}/${state.neededPlayers})") }
                    } else if (state.freeSlots > 0) {
                        state.freeSlots--
                        state.joined += event.sender
                        minigame.send(Accept(name = event.sender))
                        printChat { -green("Free player ${event.sender} joined! (${state.joined.size}/${state.neededPlayers})") }
                    } else {
                        return
                    }

                    if (state.joined.size == state.neededPlayers) {
                        printChat { -green("Starting game!") }
                        val initialState = minigame.getInitialState()
                        minigame.minigameState.state = State.InGame(event.self, state.joined, initialState)
                        minigame.send(
                            Begin(
                                names = state.joined.toList() + event.self,
                                data = minigame.encodeInitialState(initialState),
                            ),
                        )
                    } else if (state.joined.size > state.neededPlayers) {
                        printChat { -red("More players joined than are allowed") }
                        minigame.send(End(reason = "More players joined than are allowed"))
                    } else {
                        minigame.sendHosting()
                    }
                }

                else -> return
            }
        }
    }

    @Serializable
    data class Deny(
        @SerialName("i") val id: String = "d",
        @SerialName("n") val name: String,
        @SerialName("r") val reason: String,
        @SerialName("d") val data: JsonElement = JsonNull,
    ) : Packet {
        override fun <T> process(event: ChannelManagerEvent.Message, minigame: MultiplayerMinigame<T>) {
            name == event.self || return
            val state = minigame.minigameState.state as? State.Requested ?: return
            state.name == event.sender || return
            minigame.minigameState.state = null
            printChat { -red("Denied by ${event.sender}: $reason") }
        }
    }

    @Serializable
    data class Begin(
        @SerialName("i") val id: String = "b",
        @SerialName("n") val names: List<String>,
        @SerialName("d") val data: JsonElement = JsonNull,
    ) : Packet {
        override fun <T> process(event: ChannelManagerEvent.Message, minigame: MultiplayerMinigame<T>) {
            event.self in names || return
            val state = minigame.minigameState.state as? State.Accepted ?: return
            state.name == event.sender || return
            minigame.minigameState.state = State.InGame(state.name, names.toSet(), minigame.decodeInitialState(data))
            printChat { -green("Game started!") }
        }
    }

    @Serializable
    data class End(
        @SerialName("i") val id: String = "e",
        @SerialName("r") val reason: String,
        @SerialName("d") val data: JsonElement = JsonNull,
    ) : Packet {
        override fun <T> process(event: ChannelManagerEvent.Message, minigame: MultiplayerMinigame<T>) {
            when (val state = minigame.minigameState.state) {
                is State.Requested -> {
                    event.sender == state.name || return
                    minigame.minigameState.state = null
                    printChat { -red("Game canceled!") }
                }

                is State.Accepted -> {
                    event.sender == state.name || return
                    minigame.minigameState.state = null
                    printChat { -red("Game canceled!") }
                }

                is State.InGame<*> -> {
                    event.sender in state.names || return
                    minigame.minigameState.state = State.Ended(state)
                    minigame.onGameEnd(reason, data, event.sender)
                }

                else -> return
            }
        }
    }

    @Serializable
    data class Execute(
        @SerialName("i") val id: String = "x",
        @SerialName("h") val hash: String,
        @SerialName("d") val data: JsonElement = JsonNull,
    ) : Packet {
        override fun <T> process(event: ChannelManagerEvent.Message, minigame: MultiplayerMinigame<T>) {
            val state = minigame.minigameState.state.castTo<State.InGame<T>?>() ?: return
            event.sender in state.names || return
            val oldState = state.data
            val newState = minigame.execute(oldState, data, event.sender)
            if (newState !== null && minigame.hashStateForCheck(newState) == hash) {
                minigame.minigameState.state = state.copy(data = newState)
            } else {
                minigame.minigameState.state = State.Ended(state)
                printChat { -red("Game throttled!") }
                minigame.send(End(reason = "Game throttled!"))
            }
        }
    }
}

inline fun <reified T> MultiplayerMinigame<*>.send(packet: T) {
    ChannelManager.send(channel, packet)
}

fun <T> MultiplayerMinigame<T>.operate(json: JsonElement) {
    val state = (minigameState.state as? State.InGame<*>).castTo<State.InGame<T>?>() ?: return
    val oldState = state.data
    val newState = execute(oldState, json, null)
    if (newState !== null) {
        minigameState.state = state.copy(data = newState)
        send(MultiplayerMinigame.Execute(hash = hashState(newState), data = json))
    }
    postExecute(oldState, newState, json, null)
}

fun MultiplayerMinigame<*>.endGame(reason: String, json: JsonElement = JsonNull) {
    when (val state = minigameState.state) {
        is State.Hosting -> {
            minigameState.state = null
            send(MultiplayerMinigame.End(reason = reason, data = json))
        }

        is State.InGame<*> -> {
            minigameState.state = State.Ended(state)
            send(MultiplayerMinigame.End(reason = reason, data = json))
            onGameEnd(reason, json, null)
        }

        else -> return
    }
}

fun MultiplayerMinigame<*>.requestJoin(name: String) {
    endGame("exiting")
    minigameState.state = State.Requested(name)
    send(MultiplayerMinigame.Accept(name = name.lowercase()))
}

fun MultiplayerMinigame<*>.sendHosting() {
    val state = minigameState.state as? State.Hosting ?: return
    send(
        MultiplayerMinigame.MatchMake(
            invitations = state.invitations.toList(),
            freeSlots = state.freeSlots,
            currentPlayers = state.joined.size,
            neededPlayers = state.neededPlayers,
        ),
    )
}

fun MultiplayerMinigame<*>.createGame(hosting: State.Hosting) {
    endGame("exiting")
    minigameState.state = hosting
    sendHosting()
}
