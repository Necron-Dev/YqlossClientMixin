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

package yqloss.yqlossclientmixinkt.impl.hypixel

import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.EventPacket
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import net.yqloss.uktil.accessor.getValue
import net.yqloss.uktil.accessor.refs.trigger
import net.yqloss.uktil.event.EventRegistry
import net.yqloss.uktil.event.register
import yqloss.yqlossclientmixinkt.YC
import yqloss.yqlossclientmixinkt.api.YCHypixelLocation
import yqloss.yqlossclientmixinkt.api.YCHypixelServerType
import yqloss.yqlossclientmixinkt.event.hypixel.YCHypixelAPIEvent
import yqloss.yqlossclientmixinkt.event.minecraft.YCMinecraftEvent
import yqloss.yqlossclientmixinkt.impl.YCMixin
import yqloss.yqlossclientmixinkt.impl.option.YqlossClientConfig
import yqloss.yqlossclientmixinkt.module.NO_MODULE_INFO
import yqloss.yqlossclientmixinkt.module.YCModuleBase
import yqloss.yqlossclientmixinkt.module.option.YCModuleOptions
import yqloss.yqlossclientmixinkt.util.MC
import yqloss.yqlossclientmixinkt.util.printChat
import kotlin.jvm.optionals.getOrNull

class HypixelModAPIWrapper(
    private val api: HypixelModAPI,
) : YCModuleBase<YCModuleOptions>(NO_MODULE_INFO) {
    private inline fun <reified T : EventPacket> registerPacket(noinline handler: (T) -> Unit) {
        api.subscribeToEventPacket(T::class.java)
        api.createHandler(T::class.java, handler)
    }

    init {
        registerPacket<ClientboundLocationPacket> { packet ->
            val location = YCHypixelLocation(
                packet.serverName,
                packet.serverType.getOrNull()?.let { YCHypixelServerType(it.name) },
                packet.lobbyName.getOrNull(),
                packet.mode.getOrNull(),
                packet.map.getOrNull(),
            )
            if (YqlossClientConfig.main.verboseHypixelModAPI) printChat("YCMixin.api.hypixelLocation = $location")
            YCMixin.api.hypixelLocation = location
            YC.eventDispatcher(YCHypixelAPIEvent.Location(location))
        }
    }

    private val resetLocation by trigger({ MC.isIntegratedServerRunning to MC.currentServerData }) {
        if (YqlossClientConfig.main.verboseHypixelModAPI) printChat("YCMixin.api.hypixelLocation = null")
        YCMixin.api.hypixelLocation = null
        YC.eventDispatcher(YCHypixelAPIEvent.Location(null))
    }

    override val registerEvents: EventRegistry.() -> Unit
        get() = {
            super.registerEvents(this)

            register<YCMinecraftEvent.Tick.Pre> {
                resetLocation
            }
        }
}
