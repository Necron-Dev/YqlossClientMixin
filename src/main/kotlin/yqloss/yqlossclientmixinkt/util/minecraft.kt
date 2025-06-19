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

@file:Suppress("NOTHING_TO_INLINE")

package yqloss.yqlossclientmixinkt.util

import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.ISound
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.WorldRenderer
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.scoreboard.ScoreObjective
import net.minecraft.scoreboard.ScorePlayerTeam
import net.minecraft.util.*
import net.yqloss.uktil.extension.float
import net.yqloss.uktil.extension.type.stackTraceMessage
import net.yqloss.uktil.math.*
import yqloss.yqlossclientmixinkt.DEV
import yqloss.yqlossclientmixinkt.RT
import yqloss.yqlossclientmixinkt.api.internalLowerChestInventory
import yqloss.yqlossclientmixinkt.impl.MOD_VERSION
import yqloss.yqlossclientmixinkt.module.inWorld
import yqloss.yqlossclientmixinkt.module.option.YCColor
import yqloss.yqlossclientmixinkt.util.TextBuilderContext.Companion.red
import yqloss.yqlossclientmixinkt.util.TextBuilderContext.Companion.text
import yqloss.yqlossclientmixinkt.util.TextBuilderContext.Companion.yellow
import yqloss.yqlossclientmixinkt.ycLogger

val mcUtilLogger = ycLogger("Minecraft Util")

val MC: Minecraft by lazy { Minecraft.getMinecraft() }

var tickCounter = 0L

var frameCounter = 0L

var partialTicks = 0.0

var mousePosition = Vec2D(0.0, 0.0)

var windowSize = Vec2D(0.0, 0.0)

var guiScale = 1.0

inline val Vec3i.asVec3I get() = Vec3I(x, y, z)

inline val Vec3I.asBlockPos get() = BlockPos(x, y, z)

inline val Entity.lastPos get() = Vec3D(prevPosX, prevPosY, prevPosZ)

inline val Entity.currPos get() = Vec3D(posX, posY, posZ)

inline val Entity.renderPos get() = lerp(lastPos, currPos, partialTicks)

val REGEX_STYLE = Regex("\\u00a7.")

inline val String.removeStyle get() = REGEX_STYLE.replace(this, "")

inline val String.trimStyle get() = removeStyle.trim()

inline val String.keepASCII get() = filter { it.code in 32..126 }

inline val Block.item: Item get() = Item.getItemFromBlock(this)

inline fun mcRenderScope(
    mode: Int,
    vertexFormat: VertexFormat,
    function: WorldRenderer.() -> Unit,
) {
    val tessellator = Tessellator.getInstance()
    val worldRenderer = tessellator.worldRenderer
    worldRenderer.begin(mode, vertexFormat)
    try {
        function(worldRenderer)
    } finally {
        tessellator.draw()
    }
}

inline fun WorldRenderer.pos(vec: Vec3D): WorldRenderer = pos(vec.x, vec.y, vec.z)

inline fun WorldRenderer.tex(vec: Vec2D): WorldRenderer = tex(vec.x, vec.y)

inline fun WorldRenderer.color(color: YCColor): WorldRenderer = color(color.r.float, color.g.float, color.b.float, color.a.float)

inline fun printChat(component: IChatComponent) {
    mcUtilLogger.info("[PRINT CHAT] ${component.formattedText}")
    MC.theWorld ?: return
    MC.ingameGUI.chatGUI.printChatMessage(component)
}

inline fun printChat(message: String = "") = printChat(ChatComponentText(message))

inline fun printChat(noinline textBuilder: TextBuilder) = printChat(
    buildComponentOrEmpty {
        !textBuilder
    },
)

inline fun printChat(throwable: Throwable) = printChat(throwable.stackTraceMessage)

inline fun printError(noinline textBuilder: TextBuilder) = printChat {
    +red {
        +"Yqloss Client (Mixin) has encountered some error. Please report the following messages to the developer!"
        +text {
            -"Version: "
            -yellow(MOD_VERSION)
            -", Dev: "
            -yellow("$DEV")
            -", Release Type: "
            -yellow("$RT")
        }
        +textBuilder
    }
}

inline fun printError(message: String = "") = printError {
    +message
}

inline fun printError(throwable: Throwable) = printError(throwable.stackTraceMessage)

class CustomSound(
    private val argSoundLocation: ResourceLocation,
    private val argVolume: Double = 1.0,
    private val argPitch: Double = 1.0,
    private val argCanRepeat: Boolean = false,
    private val argRepeatDelay: Int = 0,
    private val argXPos: Double = 0.0,
    private val argYPos: Double = 0.0,
    private val argZPos: Double = 0.0,
    private val argAttenuationType: ISound.AttenuationType = ISound.AttenuationType.NONE,
) : ISound {
    override fun getSoundLocation() = argSoundLocation

    override fun canRepeat() = argCanRepeat

    override fun getRepeatDelay() = argRepeatDelay

    override fun getVolume() = argVolume.toFloat()

    override fun getPitch() = argPitch.toFloat()

    override fun getXPosF() = argXPos.toFloat()

    override fun getYPosF() = argYPos.toFloat()

    override fun getZPosF() = argZPos.toFloat()

    override fun getAttenuationType() = argAttenuationType
}

inline fun updateWorldRender() {
    MC.renderGlobal.loadRenderers()
}

inline fun updateWorldRenderArea(area: Area3I) {
    MC.renderGlobal.markBlockRangeForRenderUpdate(
        area.first.x,
        area.first.y,
        area.first.z,
        area.second.x - 1,
        area.second.y - 1,
        area.second.z - 1,
    )
}

inline fun updateWorldRenderBlock(pos: Vec3I) {
    MC.renderGlobal.markBlockRangeForRenderUpdate(pos.x, pos.y, pos.z, pos.x, pos.y, pos.z)
}

data class SideBar(
    val title: String,
    val list: List<Entry>,
) {
    data class Entry(
        val name: String,
        val value: Int,
    )
}

inline val sideBar: SideBar?
    get() {
        MC.theWorld ?: return null
        val scoreboard = MC.theWorld.scoreboard
        var objective: ScoreObjective? = null
        val team = scoreboard.getPlayersTeam(MC.thePlayer.name)
        if (team !== null) {
            val color = team.chatFormat.colorIndex
            if (color >= 0) {
                objective = scoreboard.getObjectiveInDisplaySlot(3 + color)
            }
        }
        objective = objective ?: scoreboard.getObjectiveInDisplaySlot(1) ?: return null
        return SideBar(
            objective.displayName,
            scoreboard
                .getSortedScores(objective)
                .reversed()
                .filter { s -> s.playerName !== null && !s.playerName.startsWith("#") }
                .take(15)
                .map {
                    SideBar.Entry(
                        ScorePlayerTeam.formatPlayerName(
                            scoreboard.getPlayersTeam(it.playerName),
                            it.playerName,
                        ),
                        it.scorePoints,
                    )
                },
        )
    }

inline val GuiChest.title: String get() = internalLowerChestInventory.displayName.formattedText

fun clickChest(slotId: Int, button: Int, mode: Int) {
    inWorld || return
    val chest = MC.currentScreen as? GuiChest ?: return
    MC.playerController.windowClick(chest.inventorySlots.windowId, slotId, button, mode, MC.thePlayer)
}

fun leftClickChest(slotId: Int) = clickChest(slotId, 0, 0)

fun rightClickChest(slotId: Int) = clickChest(slotId, 1, 0)

fun middleClickChest(slotId: Int) = clickChest(slotId, 2, 3)
