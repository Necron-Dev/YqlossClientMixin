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

@file:OptIn(ExperimentalContracts::class)

package yqloss.yqlossclientmixinkt.util

import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatStyle
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.IChatComponent
import kotlin.contracts.ExperimentalContracts

data class TextStyle(
    val color: Int? = null,
    val obfuscated: Boolean = false,
    val bold: Boolean = false,
    val strikeThrough: Boolean = false,
    val underlined: Boolean = false,
    val italic: Boolean = false,
    val clickEvent: ClickEvent? = null,
    val hoverEvent: HoverEvent? = null,
    val insertion: String? = null,
) {
    fun styleCode(from: TextStyle = TextStyle()): String {
        val builder = StringBuilder()
        if (
            color == from.color &&
            (obfuscated || !from.obfuscated) &&
            (bold || !from.bold) &&
            (strikeThrough || !from.strikeThrough) &&
            (underlined || !from.underlined) &&
            (italic || !from.italic)
        ) {
            if (obfuscated && !from.obfuscated) builder.append("\u00A7k")
            if (bold && !from.bold) builder.append("\u00A7l")
            if (strikeThrough && !from.strikeThrough) builder.append("\u00A7m")
            if (underlined && !from.underlined) builder.append("\u00A7n")
            if (italic && !from.italic) builder.append("\u00A7o")
        } else {
            builder.append('\u00A7')
            builder.append(color?.toString(16) ?: "r")
            if (obfuscated) builder.append("\u00A7k")
            if (bold) builder.append("\u00A7l")
            if (strikeThrough) builder.append("\u00A7m")
            if (underlined) builder.append("\u00A7n")
            if (italic) builder.append("\u00A7o")
        }
        return builder.toString()
    }

    fun chatStyle() = ChatStyle().also {
        it.color = if (color === null) EnumChatFormatting.RESET else EnumChatFormatting.entries[color]
        it.obfuscated = obfuscated
        it.bold = bold
        it.strikethrough = strikeThrough
        it.underlined = underlined
        it.italic = italic
        it.chatClickEvent = clickEvent
        it.chatHoverEvent = hoverEvent
        it.insertion = insertion
    }
}

data class Segment(
    val text: String,
    val style: TextStyle,
)

@DslMarker
private annotation class TextBuilderDslMarker

typealias StyleTransformer = ((TextStyle) -> TextStyle)?

typealias TextBuilder = (TextBuilderContext.() -> Unit)?

@TextBuilderDslMarker
class TextBuilderContext(
    val baseStyle: TextStyle = TextStyle(),
    val segments: MutableList<Segment> = mutableListOf(),
) {
    private fun addToSegments(segment: Segment) {
        if (segment.text.isNotEmpty()) segments += segment
    }

    fun append(textBuilder: TextBuilder) {
        textBuilder ?: return
        val textBuilderContext = TextBuilderContext(baseStyle)
        textBuilderContext.textBuilder()
        segments += textBuilderContext.segments
    }

    fun appendNewLine(textBuilder: TextBuilder) {
        textBuilder ?: return
        val textBuilderContext = TextBuilderContext(baseStyle)
        textBuilderContext.textBuilder()
        segments += textBuilderContext.segments
        if (textBuilderContext.segments.lastOrNull()?.text?.endsWith('\n') != true) {
            addToSegments(Segment("\n", baseStyle))
        }
    }

    fun appendRemoveNewLine(textBuilder: TextBuilder) {
        textBuilder ?: return
        val textBuilderContext = TextBuilderContext(baseStyle)
        textBuilderContext.textBuilder()
        val iter = textBuilderContext.segments.asReversed().iterator()
        while (iter.hasNext()) {
            val segment = iter.next()
            when {
                segment.text.all { it -> it == '\n' } -> iter.remove()

                segment.text.endsWith("\n") -> {
                    iter.remove()
                    textBuilderContext.addToSegments(segment.copy(text = segment.text.trimEnd('\n')))
                    break
                }

                else -> break
            }
        }
        segments += textBuilderContext.segments
    }

    operator fun TextBuilder.unaryMinus() = append(this)

    operator fun TextBuilder.unaryPlus() = appendNewLine(this)

    operator fun TextBuilder.not() = appendRemoveNewLine(this)

    fun append(string: String?) {
        string ?: return
        addToSegments(Segment(string, baseStyle))
    }

    fun appendNewLine(string: String?) {
        val string = when {
            string === null -> "\n"
            string.endsWith('\n') -> string
            else -> "$string\n"
        }
        addToSegments(Segment(string, baseStyle))
    }

    fun appendRemoveNewLine(string: String?) {
        string ?: return
        addToSegments(Segment(string.trimEnd('\n'), baseStyle))
    }

    operator fun String?.unaryMinus() = append(this)

    operator fun String?.unaryPlus() = appendNewLine(this)

    operator fun String?.not() = appendRemoveNewLine(this)

    fun text(string: String?): TextBuilder {
        string ?: return null
        return {
            addToSegments(Segment(string, baseStyle))
        }
    }

    infix fun StyleTransformer.modified(string: String?): TextBuilder {
        string ?: return null
        return {
            addToSegments(Segment(string, this@modified?.invoke(baseStyle) ?: baseStyle))
        }
    }

    infix fun StyleTransformer.modified(textBuilder: TextBuilder): TextBuilder {
        textBuilder ?: return null
        return {
            val textBuilderContext = TextBuilderContext(this@modified?.invoke(baseStyle) ?: baseStyle)
            textBuilderContext.textBuilder()
            segments += textBuilderContext.segments
        }
    }

    operator fun StyleTransformer.invoke(string: String?) = modified(string)

    operator fun StyleTransformer.invoke(textBuilder: TextBuilder) = modified(textBuilder)

    companion object {
        private fun colorModifier(color: Int?): (TextStyle) -> TextStyle = { it.copy(color = color) }

        val cR = colorModifier(null)
        val c0 = colorModifier(0)
        val c1 = colorModifier(1)
        val c2 = colorModifier(2)
        val c3 = colorModifier(3)
        val c4 = colorModifier(4)
        val c5 = colorModifier(5)
        val c6 = colorModifier(6)
        val c7 = colorModifier(7)
        val c8 = colorModifier(8)
        val c9 = colorModifier(9)
        val cA = colorModifier(10)
        val cB = colorModifier(11)
        val cC = colorModifier(12)
        val cD = colorModifier(13)
        val cE = colorModifier(14)
        val cF = colorModifier(15)
        val sK: (TextStyle) -> TextStyle = { it.copy(obfuscated = true) }
        val sL: (TextStyle) -> TextStyle = { it.copy(bold = true) }
        val sM: (TextStyle) -> TextStyle = { it.copy(strikeThrough = true) }
        val sN: (TextStyle) -> TextStyle = { it.copy(underlined = true) }
        val sO: (TextStyle) -> TextStyle = { it.copy(italic = true) }
        val nK: (TextStyle) -> TextStyle = { it.copy(obfuscated = false) }
        val nL: (TextStyle) -> TextStyle = { it.copy(bold = false) }
        val nM: (TextStyle) -> TextStyle = { it.copy(strikeThrough = false) }
        val nN: (TextStyle) -> TextStyle = { it.copy(underlined = false) }
        val nO: (TextStyle) -> TextStyle = { it.copy(italic = false) }
        val reset: (TextStyle) -> TextStyle = { TextStyle() }
        val noDecorations: (TextStyle) -> TextStyle = { TextStyle(color = it.color) }
        val noEvents: (TextStyle) -> TextStyle = { it.copy(clickEvent = null, hoverEvent = null, insertion = null) }
        val noClickEvent: (TextStyle) -> TextStyle = { it.copy(clickEvent = null) }
        val noHoverEvent: (TextStyle) -> TextStyle = { it.copy(hoverEvent = null) }
        val noInsertion: (TextStyle) -> TextStyle = { it.copy(insertion = null) }

        val uncolor = cR
        val resetColor = cR
        val defaultColor = cR
        val black = c0
        val darkBlue = c1
        val darkGreen = c2
        val darkAqua = c3
        val darkCyan = c3
        val darkRed = c4
        val purple = c5
        val darkPurple = c5
        val gold = c6
        val darkYellow = c6
        val gray = c7
        val lightGray = c7
        val darkGray = c8
        val blue = c9
        val lightBlue = c9
        val green = cA
        val lightGreen = cA
        val aqua = cB
        val lightAqua = cB
        val cyan = cB
        val lightCyan = cB
        val red = cC
        val lightRed = cC
        val pink = cD
        val lightPurple = cD
        val yellow = cE
        val lightYellow = cE
        val white = cF

        val obfuscated = sK
        val bold = sL
        val strikeThrough = sM
        val underlined = sN
        val italic = sO
        val noObfuscated = nK
        val noBold = nL
        val noStrikeThrough = nM
        val noUnderlined = nN
        val noItalic = nO

        fun clickEvent(action: ClickEvent.Action, value: String): (TextStyle) -> TextStyle = {
            it.copy(clickEvent = ClickEvent(action, value))
        }

        fun openUrl(value: String) = clickEvent(ClickEvent.Action.OPEN_URL, value)
        fun openFile(value: String) = clickEvent(ClickEvent.Action.OPEN_FILE, value)
        fun runCommand(value: String) = clickEvent(ClickEvent.Action.RUN_COMMAND, value)
        fun twitchUserInfo(value: String) = clickEvent(ClickEvent.Action.TWITCH_USER_INFO, value)
        fun suggestCommand(value: String) = clickEvent(ClickEvent.Action.SUGGEST_COMMAND, value)
        fun changePage(value: String) = clickEvent(ClickEvent.Action.CHANGE_PAGE, value)

        fun hoverEvent(action: HoverEvent.Action, value: IChatComponent): (TextStyle) -> TextStyle = {
            it.copy(hoverEvent = HoverEvent(action, value))
        }

        fun hoverEvent(action: HoverEvent.Action, value: String) = hoverEvent(action, ChatComponentText(value))

        fun hoverEvent(action: HoverEvent.Action, textBuilder: TextBuilder): (TextStyle) -> TextStyle {
            return hoverEvent(action, buildComponent(textBuilder)!!)
        }

        fun showText(value: IChatComponent) = hoverEvent(HoverEvent.Action.SHOW_TEXT, value)
        fun showText(value: String) = hoverEvent(HoverEvent.Action.SHOW_TEXT, value)
        fun showText(textBuilder: TextBuilder) = hoverEvent(HoverEvent.Action.SHOW_TEXT, textBuilder)
        fun showAchievement(value: IChatComponent) = hoverEvent(HoverEvent.Action.SHOW_ACHIEVEMENT, value)
        fun showAchievement(value: String) = hoverEvent(HoverEvent.Action.SHOW_ACHIEVEMENT, value)
        fun showAchievement(textBuilder: TextBuilder) = hoverEvent(HoverEvent.Action.SHOW_ACHIEVEMENT, textBuilder)

        fun showItem(value: IChatComponent) = hoverEvent(HoverEvent.Action.SHOW_ITEM, value)
        fun showItem(value: String) = hoverEvent(HoverEvent.Action.SHOW_ITEM, value)
        fun showItem(textBuilder: TextBuilder) = hoverEvent(HoverEvent.Action.SHOW_ITEM, textBuilder)
        fun showEntity(value: IChatComponent) = hoverEvent(HoverEvent.Action.SHOW_ENTITY, value)
        fun showEntity(value: String) = hoverEvent(HoverEvent.Action.SHOW_ENTITY, value)
        fun showEntity(textBuilder: TextBuilder) = hoverEvent(HoverEvent.Action.SHOW_ENTITY, textBuilder)

        fun insertion(string: String?): (TextStyle) -> TextStyle = { it.copy(insertion = string) }

        fun text(textBuilder: TextBuilder) = textBuilder

        operator fun StyleTransformer.times(other: StyleTransformer) = when {
            this === null && other === null -> null
            this === null -> other
            other === null -> this

            else -> { it: TextStyle ->
                other(this(it))
            }
        }
    }
}

fun buildString(textBuilder: TextBuilder): String? {
    textBuilder ?: return null
    val textBuilderContext = TextBuilderContext()
    textBuilderContext.textBuilder()
    val stringBuilder = StringBuilder()
    var lastStyle = TextStyle()
    textBuilderContext.segments.forEach {
        stringBuilder.append(it.style.styleCode(lastStyle))
        stringBuilder.append(it.text)
        lastStyle = it.style
    }
    return stringBuilder.toString()
}

fun buildStringOrEmpty(textBuilder: TextBuilder): String {
    textBuilder ?: return ""
    val textBuilderContext = TextBuilderContext()
    textBuilderContext.textBuilder()
    val stringBuilder = StringBuilder()
    var lastStyle = TextStyle()
    textBuilderContext.segments.forEach {
        stringBuilder.append(it.style.styleCode(lastStyle))
        stringBuilder.append(it.text)
        lastStyle = it.style
    }
    return stringBuilder.toString()
}

fun buildComponent(textBuilder: TextBuilder): IChatComponent? {
    textBuilder ?: return null
    val textBuilderContext = TextBuilderContext()
    textBuilderContext.textBuilder()
    val rootComponent = ChatComponentText("")
    textBuilderContext.segments.forEach {
        rootComponent.appendSibling(
            ChatComponentText(it.text).apply {
                chatStyle = it.style.chatStyle()
            },
        )
    }
    return rootComponent
}

fun buildComponentOrEmpty(textBuilder: TextBuilder): IChatComponent {
    textBuilder ?: return ChatComponentText("")
    val textBuilderContext = TextBuilderContext()
    textBuilderContext.textBuilder()
    val rootComponent = ChatComponentText("")
    textBuilderContext.segments.forEach {
        rootComponent.appendSibling(
            ChatComponentText(it.text).apply {
                chatStyle = it.style.chatStyle()
            },
        )
    }
    return rootComponent
}
