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

import net.yqloss.uktil.math.Vec2D
import net.yqloss.uktil.math.lerp
import yqloss.yqlossclientmixinkt.impl.nanovgui.NanoVGUIContext
import yqloss.yqlossclientmixinkt.impl.nanovgui.Widget
import yqloss.yqlossclientmixinkt.impl.oneconfiginternal.nvg
import yqloss.yqlossclientmixinkt.util.alphaScale

data class RingArcWidget(
    private val center: Vec2D,
    private val outerRadius: Double,
    private val innerRadius: Double,
    private val fromRadian: Double,
    private val toRadian: Double,
    private val arcPaddingFrom: Double,
    private val arcPaddingTo: Double,
    private val color: Int,
) : Widget<RingArcWidget> {
    override fun draw(context: NanoVGUIContext) {
        nvg.run {
            drawRingArc(
                context.vg,
                center.x,
                center.y,
                outerRadius,
                innerRadius,
                fromRadian,
                toRadian,
                arcPaddingFrom,
                arcPaddingTo,
                color,
            )
        }
    }

    override fun alphaScale(alpha: Double) = copy(color = color alphaScale alpha)

    override fun scale(
        scale: Double,
        origin: Vec2D,
    ) = copy(center = lerp(origin, center, scale), outerRadius = outerRadius * scale, innerRadius = innerRadius * scale)
}
