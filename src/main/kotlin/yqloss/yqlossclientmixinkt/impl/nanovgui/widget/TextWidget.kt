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

package yqloss.yqlossclientmixinkt.impl.nanovgui.widget

import cc.polyfrost.oneconfig.renderer.font.Font
import cc.polyfrost.oneconfig.renderer.font.Fonts
import net.yqloss.uktil.extension.double
import net.yqloss.uktil.extension.float
import net.yqloss.uktil.math.Vec2D
import net.yqloss.uktil.math.lerp
import yqloss.yqlossclientmixinkt.impl.nanovgui.NanoVGUIContext
import yqloss.yqlossclientmixinkt.impl.nanovgui.Widget
import yqloss.yqlossclientmixinkt.util.alphaScale

data class TextWidget(
    private val text: String,
    private val pos: Vec2D,
    private val color: Int,
    private val size: Double,
    private val font: () -> Font = { Fonts.REGULAR },
    private val anchor: Vec2D = Vec2D(0.0, 0.0),
    private val widthLimit: Double? = null,
    private val ellipsis: String = "",
) : Widget<TextWidget> {
    override fun draw(context: NanoVGUIContext) {
        context.run {
            val font = font()
            var width = helper.getTextWidth(vg, text, size.float, font).double
            var textToRender = text
            if (widthLimit !== null && width >= widthLimit) {
                width = helper.getTextWidth(vg, ellipsis, size.float, font).double
                textToRender = StringBuilder()
                    .apply {
                        text.firstOrNull {
                            val newWidth = width + helper.getTextWidth(vg, it.toString(), size.float, font).double
                            if (newWidth < widthLimit) {
                                width = newWidth
                                append(it)
                                false
                            } else {
                                true
                            }
                        }
                    }.append(ellipsis)
                    .toString()
            }
            val x = pos.x - width * anchor.x
            val y = pos.y - size * anchor.y + size / 2.0
            helper.drawText(vg, textToRender, x.float, y.float, color, size.float, font)
        }
    }

    override fun alphaScale(alpha: Double) = copy(color = color alphaScale alpha)

    override fun scale(
        scale: Double,
        origin: Vec2D,
    ) = copy(pos = lerp(origin, pos, scale), size = size * scale, widthLimit = widthLimit?.times(scale))
}
