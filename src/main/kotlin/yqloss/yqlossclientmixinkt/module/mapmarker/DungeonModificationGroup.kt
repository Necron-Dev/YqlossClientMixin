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

import net.yqloss.uktil.scope.noExcept
import yqloss.yqlossclientmixinkt.util.keepASCII
import yqloss.yqlossclientmixinkt.util.sideBar
import yqloss.yqlossclientmixinkt.util.trimStyle
import yqloss.yqlossclientmixinkt.util.updateWorldRender

private val ALLOWED_ROOMS = setOf("f1", "f2", "f3", "f4", "f5", "f6", "f7")

class DungeonModificationGroup(
    private val root: String,
) : ModificationGroup {
    private var wrapped: ModificationGroup = FolderModificationGroup(root)

    private var lastRoom = ""

    override fun onTick() {
        var room = ""
        noExcept {
            sideBar?.let { sideBar ->
                if (sideBar.list.isEmpty()) return@let
                sideBar.list[0]
                    .name.trimStyle.keepASCII
                    .trim()
                    .split(" ")[2]
                    .takeIf { it in ALLOWED_ROOMS }
                    ?.let { room = it }
            }
        }
        if (lastRoom != room) {
            lastRoom = room
            wrapped = FolderModificationGroup("$root/$room")
            updateWorldRender()
        }
    }

    override fun get(name: String) = wrapped[name]

    override fun listModifications() = wrapped.listModifications()
}
