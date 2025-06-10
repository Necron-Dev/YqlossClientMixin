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

package yqloss.yqlossclientmixinkt.module.mapmarker

import net.yqloss.uktil.accessor.getValue
import net.yqloss.uktil.accessor.refs.trigger
import net.yqloss.uktil.event.EventRegistry
import net.yqloss.uktil.event.register
import net.yqloss.uktil.functional.plus
import net.yqloss.uktil.scope.longRet
import net.yqloss.uktil.scope.noExcept
import yqloss.yqlossclientmixinkt.YC
import yqloss.yqlossclientmixinkt.event.minecraft.YCCommandEvent
import yqloss.yqlossclientmixinkt.event.minecraft.YCMinecraftEvent
import yqloss.yqlossclientmixinkt.event.minecraft.YCRenderEvent
import yqloss.yqlossclientmixinkt.module.YCModuleBase
import yqloss.yqlossclientmixinkt.module.enabled
import yqloss.yqlossclientmixinkt.module.inWorld
import yqloss.yqlossclientmixinkt.module.moduleInfo
import yqloss.yqlossclientmixinkt.util.printError
import yqloss.yqlossclientmixinkt.util.updateWorldRender
import java.io.File

val INFO_MAP_MARKER = moduleInfo<MapMarkerOptions>("map_marker", "Map Marker")

object MapMarker : YCModuleBase<MapMarkerOptions>(INFO_MAP_MARKER) {
    private var modificationGroup: ModificationGroup? = null

    fun loadModification(path: String): Modification {
        return JsonModification.fromFile(File("./yqlossclient-mapmarker/$path.json"))
    }

    fun loadModificationGroup(path: String) = when {
        path == "/hypixel/SkyBlock/Dungeon" -> DungeonModificationGroup(path)
        else -> FolderModificationGroup(path)
    }

    private val reloadChunksOnSwitch by trigger(::enabled) {
        inWorld || return@trigger
        updateWorldRender()
    }

    private val loadModificationOnLocationChange by trigger({ YC.api.hypixelLocation }) {
        modificationGroup = null
        val serverType = YC.api.hypixelLocation
            ?.serverType
            ?.name ?: return@trigger
        val map = YC.api.hypixelLocation?.map ?: return@trigger
        modificationGroup = loadModificationGroup("/hypixel/$serverType/$map")
    }

    private val reloadChunksOnModificationChange by trigger({ modificationGroup }) {
        updateWorldRender()
    }

    override val registerEvents: EventRegistry.() -> Unit
        get() = {
            super.registerEvents(this)

            register<YCMinecraftEvent.Tick.Pre> {
                reloadChunksOnSwitch

                enabled || longRet

                loadModificationOnLocationChange

                inWorld || longRet

                reloadChunksOnModificationChange

                modificationGroup?.onTick()
            }

            register<YCRenderEvent.Block.ProcessAreaBlockState> { event ->
                enabled || longRet

                val modifications = modificationGroup?.listModifications() ?: longRet

                modifications.forEach { modification ->
                    if (modification.containsSubChunk(event.area)) {
                        event.mutableProcessor += { args ->
                            args.mutableBlockState =
                                modification.invoke(args.position, args.blockState, event.blockAccess)
                                    ?: args.mutableBlockState
                        }
                    }
                }
            }

            register<YCCommandEvent.Execute> { event ->
                !event.canceled && enabled && !event.disableClientCommand || longRet

                noExcept(::printError) {
                    when (event.args.getOrNull(0)) {
                        "/ycmmc", "/yqlossclientmapmarkercurrent" -> {
                            event.canceled = true
                            modificationGroup!![event.args[1]].onCommand(event.args.subList(2, event.args.size))
                        }

                        "/ycmmt", "/yqlossclientmapmarkertarget" -> {
                            event.canceled = true
                            loadModification(event.args[1]).onCommand(event.args.subList(2, event.args.size))
                        }
                    }
                }
            }
        }
}
