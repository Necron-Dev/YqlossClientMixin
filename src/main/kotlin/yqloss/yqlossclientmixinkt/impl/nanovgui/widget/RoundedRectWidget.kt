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

import net.yqloss.uktil.extension.float
import net.yqloss.uktil.math.Vec2D
import net.yqloss.uktil.math.lerp
import yqloss.yqlossclientmixinkt.impl.nanovgui.NanoVGUIContext
import yqloss.yqlossclientmixinkt.impl.nanovgui.Widget
import yqloss.yqlossclientmixinkt.util.alphaScale

data class RoundedRectWidget(
    private val pos1: Vec2D,
    private val pos2: Vec2D,
    private val color: Int,
    private val radius: Double,
) : Widget<RoundedRectWidget> {
    override fun draw(context: NanoVGUIContext) {
        context.run {
            val box = pos2 - pos1
            helper.drawRoundedRect(
                vg,
                pos1.x.float,
                pos1.y.float,
                box.x.float,
                box.y.float,
                color,
                radius.float,
            )
        }
    }

    override fun alphaScale(alpha: Double) = copy(color = color alphaScale alpha)

    override fun scale(
        scale: Double,
        origin: Vec2D,
    ) = copy(pos1 = lerp(origin, pos1, scale), pos2 = lerp(origin, pos2, scale), radius = radius * scale)
}
