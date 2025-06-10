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

import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayClient
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.util.IChatComponent
import net.yqloss.uktil.event.CancelableEvent
import net.yqloss.uktil.event.Event
import yqloss.yqlossclientmixinkt.util.removeStyle
import yqloss.yqlossclientmixinkt.util.trimStyle

sealed interface YCPacketEvent<TPacket : Packet<INetHandlerPlayClient>> : Event {
    val packet: TPacket

    sealed interface S02 : YCPacketEvent<S02PacketChat> {
        val component: IChatComponent
        val styledText: String get() = component.formattedText
        val plainText get() = component.formattedText.removeStyle
        val trimmedPlainText get() = component.formattedText.trimStyle

        sealed interface Chat : S02 {
            data class Pre(
                override val packet: S02PacketChat,
                override val component: IChatComponent = packet.chatComponent,
                var mutableComponent: IChatComponent = packet.chatComponent,
                override var canceled: Boolean = false,
            ) : Chat,
                CancelableEvent

            data class Post(
                override val packet: S02PacketChat,
                val canceled: Boolean,
                val modifiedComponent: IChatComponent,
                override val component: IChatComponent = packet.chatComponent,
            ) : Chat
        }
    }
}
