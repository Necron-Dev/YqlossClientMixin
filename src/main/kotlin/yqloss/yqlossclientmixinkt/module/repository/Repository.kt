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

package yqloss.yqlossclientmixinkt.module.repository

import net.yqloss.uktil.accessor.getValue
import net.yqloss.uktil.accessor.refs.lazyVarOf
import net.yqloss.uktil.accessor.setValue
import net.yqloss.uktil.event.EventRegistry
import net.yqloss.uktil.event.register
import net.yqloss.uktil.scope.longRet
import yqloss.yqlossclientmixinkt.event.minecraft.YCMinecraftEvent
import yqloss.yqlossclientmixinkt.module.YCModuleBase
import yqloss.yqlossclientmixinkt.module.enabled
import yqloss.yqlossclientmixinkt.module.moduleInfo
import yqloss.yqlossclientmixinkt.network.Resource
import yqloss.yqlossclientmixinkt.network.requestAll

val INFO_REPOSITORY = moduleInfo<RepositoryOptions>("repository", "Repository")

object Repository : YCModuleBase<RepositoryOptions>(INFO_REPOSITORY) {
    var version by lazyVarOf { Version() }
    var capes by lazyVarOf { Capes() }

    fun reloadVersion() {
        version = Version()
    }

    fun reloadCapes() {
        capes = Capes()
    }

    val repositoryData: List<Resource>
        get() = listOfNotNull(
            version.takeIf { options.versionEnabled },
            capes.takeIf { options.capeEnabled },
        )

    override val registerEvents: EventRegistry.() -> Unit
        get() = {
            super.registerEvents(this)

            register<YCMinecraftEvent.Tick.Pre> {
                enabled || longRet

                repositoryData.requestAll()

                version.onTickPre()
                capes.onTickPre()
            }

            register<RepositoryEvent.LoadCape> { event ->
                enabled || longRet

                event.mutableLocation = capes.onLoadCape(event.uuid)
            }
        }
}
