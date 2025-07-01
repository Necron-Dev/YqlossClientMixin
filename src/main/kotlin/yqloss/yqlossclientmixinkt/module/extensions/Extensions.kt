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

package yqloss.yqlossclientmixinkt.module.extensions

import net.yqloss.uktil.event.EventRegistry
import net.yqloss.uktil.event.register
import net.yqloss.uktil.scope.longRet
import net.yqloss.uktil.scope.noExcept
import yqloss.yqlossclientmixinkt.api.formatTranslated
import yqloss.yqlossclientmixinkt.event.minecraft.YCCommandEvent
import yqloss.yqlossclientmixinkt.event.minecraft.YCMinecraftEvent
import yqloss.yqlossclientmixinkt.module.YCModuleBase
import yqloss.yqlossclientmixinkt.module.enabled
import yqloss.yqlossclientmixinkt.module.moduleInfo
import yqloss.yqlossclientmixinkt.util.TextBuilder
import yqloss.yqlossclientmixinkt.util.TextBuilderContext.Companion.blue
import yqloss.yqlossclientmixinkt.util.TextBuilderContext.Companion.invoke
import yqloss.yqlossclientmixinkt.util.TextBuilderContext.Companion.strikeThrough
import yqloss.yqlossclientmixinkt.util.TextBuilderContext.Companion.text
import yqloss.yqlossclientmixinkt.util.TextBuilderContext.Companion.white
import yqloss.yqlossclientmixinkt.util.TextBuilderContext.Companion.yellow
import yqloss.yqlossclientmixinkt.util.printChat
import yqloss.yqlossclientmixinkt.util.printError

// name regex: [A-Za-z0-9_]+
// repository:
//   official:
//     https://ycext.yqloss.net
//   official_XXX:
//     https://ycextXXX.yqloss.net
//   github:
//     https://ycextAUTHOR.github.io
//   any valid URL without trailing slash:
//     https://somewebsite.com/somepath

val INFO_EXTENSIONS = moduleInfo<ExtensionsOptions>("extensions", "Extensions")

private val SPLIT_LINE = text { +blue(strikeThrough("-".repeat(32))) }

interface ExtensionOperation {
    val text: TextBuilder
}

object Extensions : YCModuleBase<ExtensionsOptions>(INFO_EXTENSIONS) {
    fun reloadAllExtensions(): List<ExtensionOperation> = buildList {
    }

    private fun printOperations(operationList: List<ExtensionOperation>) = printChat {
        +SPLIT_LINE
        operationList.forEach {
            +it.text
        }
        +SPLIT_LINE
    }

    private fun processLoadCommand(args: List<String>) {
        printOperations(reloadAllExtensions())
    }

    private fun processImportCommand(args: List<String>) {
    }

    private fun processDeleteCommand(args: List<String>) {
    }

    private fun processUpdateCommand(args: List<String>) {
    }

    private fun processUpdateAllCommand(args: List<String>) {
    }

    private fun commandHelp(subCommand: String, description: String) = text {
        -yellow {
            -"/ycext "
            -subCommand
        }
        -" "
        -white(description)
    }

    private fun processCommand(args: List<String>) {
        when (args.getOrNull(1)) {
            "load" -> processLoadCommand(args)

            "import" -> processImportCommand(args)

            "delete" -> processDeleteCommand(args)

            "update" -> processUpdateCommand(args)

            "updateall" -> processUpdateAllCommand(args)

            else -> printChat {
                +SPLIT_LINE
                +formatTranslated("{module.extensions.message.help}")
                +SPLIT_LINE
            }
        }
    }

    override val registerEvents: EventRegistry.() -> Unit
        get() = {
            super.registerEvents(this)

            register<YCMinecraftEvent.Load.Post> {
            }

            register<YCCommandEvent.Execute> { event ->
                !event.canceled && enabled && !event.disableClientCommand || longRet

                noExcept(::printError) {
                    when (event.args.getOrNull(0)) {
                        "/ycext", "/yqlossclientextensions" -> {
                            event.canceled = true
                            processCommand(event.args)
                        }
                    }
                }
            }
        }
}
