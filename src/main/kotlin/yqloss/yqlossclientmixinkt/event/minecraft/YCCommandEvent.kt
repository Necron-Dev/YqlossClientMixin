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

package yqloss.yqlossclientmixinkt.event.minecraft

import net.yqloss.uktil.event.CancelableEvent
import net.yqloss.uktil.event.Event
import net.yqloss.uktil.scope.noExcept
import yqloss.yqlossclientmixinkt.util.LOG_COMMAND_ARGUMENT_PARSING
import yqloss.yqlossclientmixinkt.ycLogger

private val logger = ycLogger("Command")

sealed interface YCCommandEvent : Event {
    fun processArguments(command: String): List<String> {
        return noExcept {
            val trim = command.trim()
            if (trim.isEmpty()) return emptyList()
            var pointer = 0

            fun readString(): String {
                return StringBuilder()
                    .apply {
                        ++pointer
                        while (trim[pointer] != '"') {
                            val char = trim[pointer]
                            if (char == '\\') {
                                ++pointer
                                when (trim[pointer]) {
                                    '\\' -> append('\\')
                                    '\'' -> append('\'')
                                    '"' -> append('"')
                                    'n' -> append('\n')
                                    'r' -> append('\r')
                                    't' -> append('\t')
                                    's' -> append('\u00A7')

                                    'x' -> {
                                        append(Char(trim.substring(pointer + 1..pointer + 2).toInt(16)))
                                        pointer += 2
                                    }

                                    'u' -> {
                                        append(Char(trim.substring(pointer + 1..pointer + 4).toInt(16)))
                                        pointer += 4
                                    }
                                }
                            } else {
                                append(char)
                            }
                            ++pointer
                        }
                        ++pointer
                    }.toString()
            }

            fun readToken(): String {
                return if (trim[pointer] == '"') {
                    readString()
                } else {
                    val newPointer = trim.indexOf(' ', pointer).takeIf { it != -1 } ?: trim.length
                    trim.substring(pointer..<newPointer).also { pointer = newPointer }
                }
            }

            fun skipSpaces() {
                while (pointer < trim.length && trim[pointer] == ' ') {
                    ++pointer
                }
            }

            buildList {
                while (pointer < trim.length) {
                    add(readToken())
                    skipSpaces()
                }
            }
        } ?: emptyList()
    }

    data class Execute(
        val command: String,
        val disableClientCommand: Boolean,
        override var canceled: Boolean = false,
    ) : YCCommandEvent,
        CancelableEvent {
        val args by lazy {
            processArguments(command).also {
                if (LOG_COMMAND_ARGUMENT_PARSING) {
                    logger.info("parsed the following command into $it")
                    logger.info(command)
                }
            }
        }
    }
}
