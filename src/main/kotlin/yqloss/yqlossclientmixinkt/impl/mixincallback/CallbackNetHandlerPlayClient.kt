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

package yqloss.yqlossclientmixinkt.impl.mixincallback

import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.util.IChatComponent
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import yqloss.yqlossclientmixinkt.YC
import yqloss.yqlossclientmixinkt.event.hypixel.YCHypixelEvent
import yqloss.yqlossclientmixinkt.event.hypixel.hypixelServerTickCounter
import yqloss.yqlossclientmixinkt.event.hypixel.hypixelServerTickDuration
import yqloss.yqlossclientmixinkt.event.hypixel.hypixelServerTickUpdateTime
import yqloss.yqlossclientmixinkt.event.minecraft.YCPacketEvent
import yqloss.yqlossclientmixinkt.impl.option.YqlossClientConfig
import yqloss.yqlossclientmixinkt.util.printChatTranslated

object CallbackNetHandlerPlayClient {
    object YqlossClient {
        private var canceledS02 = false
        private lateinit var mutableComponentS02: IChatComponent

        fun handleChatPre1(packet: S02PacketChat): S02PacketChat {
            YCPacketEvent.S02.Chat
                .Pre(packet)
                .also(YC.eventDispatcher)
                .apply {
                    canceledS02 = canceled
                    mutableComponentS02 = mutableComponent
                    return S02PacketChat(mutableComponent, packet.type)
                }
        }

        fun handleChatPre2(
            packet: S02PacketChat,
            ci: CallbackInfo,
        ) {
            if (canceledS02) {
                ci.cancel()
                YCPacketEvent.S02.Chat
                    .Post(packet, canceledS02, mutableComponentS02)
                    .also(YC.eventDispatcher)
            }
        }

        fun handleChatPost(packet: S02PacketChat) {
            if (!canceledS02) {
                YCPacketEvent.S02.Chat
                    .Post(packet, canceledS02, mutableComponentS02)
                    .also(YC.eventDispatcher)
            }
        }

        private val serverTickQueue = ArrayDeque<Long>()

        fun handleConfirmTransactionPre() {
            YC.api.hypixelLocation ?: return
            val time = System.nanoTime()
            ++hypixelServerTickCounter
            serverTickQueue += time
            hypixelServerTickUpdateTime = time
            val samples = YqlossClientConfig.main.hypixelPartialTickSamples
            while (serverTickQueue.size > YqlossClientConfig.main.hypixelPartialTickSamples) {
                serverTickQueue.removeFirst()
            }
            if (serverTickQueue.size == samples && samples >= 2) {
                hypixelServerTickDuration = (serverTickQueue.last() - serverTickQueue.first()) / (samples - 1)
                if (YqlossClientConfig.main.verboseHypixelServerTickDuration) {
                    printChatTranslated("{module.main.message.hypixel_server_tick_duration}") {
                        this["duration"] = hypixelServerTickDuration
                    }
                }
            } else {
                hypixelServerTickDuration = 50_000_000
                if (YqlossClientConfig.main.verboseHypixelServerTickDuration) {
                    printChatTranslated("{module.main.message.hypixel_server_tick_duration_unknown}")
                }
            }
            YC.eventDispatcher(YCHypixelEvent.ServerTick)
        }
    }
}
