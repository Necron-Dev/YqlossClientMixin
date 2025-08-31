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

package yqloss.yqlossclientmixinkt.module.channelmanager

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive
import net.yqloss.uktil.event.EventRegistry
import net.yqloss.uktil.event.register
import net.yqloss.uktil.extension.byte
import net.yqloss.uktil.extension.int
import net.yqloss.uktil.scope.longRet
import net.yqloss.uktil.scope.noExcept
import yqloss.yqlossclientmixinkt.YC
import yqloss.yqlossclientmixinkt.YCJson
import yqloss.yqlossclientmixinkt.event.minecraft.YCCommandEvent
import yqloss.yqlossclientmixinkt.event.minecraft.YCPacketEvent
import yqloss.yqlossclientmixinkt.module.YCModuleBase
import yqloss.yqlossclientmixinkt.module.enabled
import yqloss.yqlossclientmixinkt.module.inHypixel
import yqloss.yqlossclientmixinkt.module.moduleInfo
import yqloss.yqlossclientmixinkt.module.option.invoke
import yqloss.yqlossclientmixinkt.util.*
import yqloss.yqlossclientmixinkt.util.TextBuilderContext.Companion.darkGray
import yqloss.yqlossclientmixinkt.util.TextBuilderContext.Companion.invoke
import yqloss.yqlossclientmixinkt.util.TextBuilderContext.Companion.pink
import yqloss.yqlossclientmixinkt.util.TextBuilderContext.Companion.purple
import yqloss.yqlossclientmixinkt.util.TextBuilderContext.Companion.red
import yqloss.yqlossclientmixinkt.util.TextBuilderContext.Companion.white
import kotlin.experimental.xor
import kotlin.math.min
import kotlin.random.Random

val INFO_CHANNEL_MANAGER = moduleInfo<ChannelManagerOptions>("channel_manager", "Channel Manager")

const val BLOCK_SIZE_UNIT = 32

private val PARTY_MESSAGE = Regex("Party > (?:\\[.*?] )?([A-Za-z0-9_]+): (.*)")

object ChannelManager : YCModuleBase<ChannelManagerOptions>(INFO_CHANNEL_MANAGER) {
    private fun fromCharset(char: Char): Byte? {
        char in '\u2800'..'\u28FF' || return null
        return (char - '\u2800').byte
    }

    private fun toCharset(byte: Byte) = Char(byte.int and 0xFF or 0x2800)

    private fun encodeInt(int: Int) = byteArrayOf(
        (int and 0xFF).byte,
        (int shr 8 and 0xFF).byte,
        (int shr 16 and 0xFF).byte,
        (int shr 24 and 0xFF).byte,
    )

    private fun decodeInt(bytes: ByteArray) = 0 or
        (bytes[0].int and 0xFF) or
        (bytes[1].int and 0xFF shl 8) or
        (bytes[2].int and 0xFF shl 16) or
        (bytes[3].int and 0xFF shl 24)

    fun sendBytes(bytes: ByteArray, userName: String = MC.session.profile.name) {
        val compressed = compressGzip(bytes)

        val blockSizeBytes = options.blockSize * BLOCK_SIZE_UNIT
        var blockIndex = 0
        var blockStartIndex = 0

        val blocks = mutableListOf<ByteArray>()

        while (blockStartIndex < compressed.size) {
            val dataSize = min(compressed.size - blockStartIndex, blockSizeBytes)
            val block = ByteArray(8 + dataSize)

            compressed.copyInto(block, 8, blockStartIndex, blockStartIndex + dataSize)

            val meta = (if (blockIndex == 0) 1 else 0) shl 7 or (options.blockSize - 1 and 0xF)
            block[3] = meta.byte

            val serial = if (blockIndex == 0) compressed.size else blockIndex
            encodeInt(serial).copyInto(block, 4)

            var checksum = 0
            repeat(dataSize + 5) {
                checksum *= 31
                checksum += block[it + 3].int and 0xFF
            }
            encodeInt(checksum).copyInto(block, 1, 0, 2)

            val seed = Random.nextBytes(1) + userName.toByteArray(Charsets.UTF_8)
            block[0] = seed[0]

            val xorKey = md5(seed)
            repeat(dataSize + 7) {
                block[it + 1] = block[it + 1] xor xorKey[it % 13]
            }

            blocks += block

            blockIndex++
            blockStartIndex += blockSizeBytes
        }

        blocks
            .map { block -> block.joinToString("") { toCharset(it).toString() } }
            .forEach {
                options.onSendMessage(logger) {
                    this["message"] = it
                }
            }
    }

    @Serializable
    data class Packet(
        @SerialName("c") val channel: String,
        @SerialName("d") val data: JsonElement,
    )

    inline fun <reified T> send(channel: String, data: T, userName: String = MC.session.profile.name) {
        val packet = Packet(channel, YCJson.encodeToJsonElement(data))
        sendBytes(YCJson.encodeToString(packet).toByteArray(Charsets.UTF_8), userName)
    }

    data class ReceivingContext(
        val totalLength: Int,
        var currentLength: Int = 0,
        var lastPacketSerial: Int = -1,
        val blocks: MutableList<ByteArray> = mutableListOf(),
    )

    private val receivingPackets = mutableMapOf<String, ReceivingContext>()

    private fun dispatchMessage(sender: String, data: ByteArray, self: String) {
        val packet = YCJson.decodeFromString<Packet>(decompressGzip(data).toString(Charsets.UTF_8))
        logger.info("Packet: $packet")
        YC.eventDispatcher(ChannelManagerEvent.Message(packet.channel, sender, packet.data, self))
    }

    private fun receiveBlock(sender: String, message: String, userName: String = MC.session.profile.name) = noExcept(logger::warn) {
        val block = message
            .mapNotNull(::fromCharset)
            .toByteArray()
            .apply { size > 8 || return@noExcept }

        val xorKey = md5(block.copyOf(1) + sender.toByteArray(Charsets.UTF_8))
        repeat(block.size - 1) {
            block[it + 1] = block[it + 1] xor xorKey[it % 13]
        }

        val blockChecksum = decodeInt(block.copyOfRange(1, 5))
        var checksum = 0
        repeat(block.size - 3) {
            checksum *= 31
            checksum += block[it + 3].int and 0xFF
        }
        checksum xor blockChecksum and 0xFFFF == 0 || return@noExcept

        val meta = block[3].int
        val firstBlock = meta and 0x80 != 0
        val blockSizeBytes = ((meta and 0xF) + 1) * BLOCK_SIZE_UNIT

        val serial = decodeInt(block.copyOfRange(4, 8))

        val data = block.copyOfRange(8, block.size)
        data.size <= blockSizeBytes || return@noExcept printChat { -red("discarding oversized data") }

        var context = receivingPackets.remove(sender)

        if (firstBlock) {
            if (context !== null) printChat { -red("discarding the previous packet") }
            context = ReceivingContext(serial)
        }

        context ?: return@noExcept printChat { -red("discarding incomplete packet") }

        firstBlock || serial == context.lastPacketSerial + 1 || return@noExcept printChat { -red("discarding broken packet") }

        context.lastPacketSerial++
        context.currentLength += data.size
        context.blocks += data

        if (context.currentLength == context.totalLength) {
            val joined = ByteArray(context.totalLength)
            var i = 0
            context.blocks.forEach {
                it.copyInto(joined, i)
                i += it.size
            }
            dispatchMessage(sender, joined, userName)
        } else if (context.currentLength > context.totalLength) {
            return@noExcept printChat { -red("discarding oversized packet") }
        } else {
            receivingPackets[sender] = context
            logger.info("Context: $context")
        }
    }

    override val registerEvents: EventRegistry.() -> Unit
        get() = {
            super.registerEvents(this)

            register<YCPacketEvent.S02.Chat.Post> { event ->
                !event.canceled && enabled && inHypixel || longRet

                val result = PARTY_MESSAGE.matchEntire(event.trimmedPlainText) ?: longRet

                val sender = result.groupValues[1]
                val message = result.groupValues[2]

                receiveBlock(sender, message)
            }

            register<YCCommandEvent.Execute> { event ->
                !event.canceled && enabled && !event.disableClientCommand || longRet

                noExcept(::printError) {
                    when (event.args.getOrNull(0)) {
                        "/ycc", "/yqlossclientchat" -> {
                            event.canceled = true
                            send(".c", event.args.drop(1).joinToString(" "))
                        }
                    }
                }
            }

            register<ChannelManagerEvent.Message> { event ->
                !event.canceled && event.channel == ".c" || longRet
                printChat {
                    -purple("YCC ")
                    -darkGray("> ")
                    -pink(event.sender)
                    -white(": ${event.message.jsonPrimitive.content}")
                }
            }
        }
}
