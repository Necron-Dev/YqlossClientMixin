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

package yqloss.yqlossclientmixinkt.impl.nanovgui.gui

import net.yqloss.uktil.math.Vec2D
import yqloss.yqlossclientmixinkt.impl.nanovgui.Transformation
import yqloss.yqlossclientmixinkt.impl.nanovgui.Widget
import yqloss.yqlossclientmixinkt.impl.nanovgui.widget.RingArcWidget
import yqloss.yqlossclientmixinkt.util.mousePosition
import kotlin.math.PI
import kotlin.math.atan2

private const val DOUBLE_PI = 2.0 * PI

abstract class RingArcButton<T>(
    info: T,
) : Button<T>(info) {
    abstract val outerRadius: Double

    abstract val innerRadius: Double

    abstract val fromRadian: Double

    abstract val toRadian: Double

    abstract fun getArcPadding(hovered: Boolean): Double

    override val collisionSize = Vec2D(0.0, 0.0)

    override fun isHovered(tr: Transformation): Boolean {
        val mouse = !tr pos mousePosition
        val ro = outerRadius * outerRadius
        val ri = innerRadius * innerRadius
        val mouseRadius = mouse.x * mouse.x + mouse.y * mouse.y
        val mouseAngle = atan2(mouse.y, mouse.x)
        var rf = mouseAngle - fromRadian
        rf = (rf % DOUBLE_PI + DOUBLE_PI) % DOUBLE_PI
        var rt = toRadian - mouseAngle
        rt = (rt % DOUBLE_PI + DOUBLE_PI) % DOUBLE_PI
        return mouseRadius in ri..<ro && rf + rt < DOUBLE_PI
    }

    override fun renderBackground(
        widgets: MutableList<Widget<*>>,
        tr: Transformation,
        hovered: Boolean,
    ) {
        val argb = getActualColor(hovered)
        val padding = getArcPadding(hovered)
        widgets.add(
            RingArcWidget(
                tr pos Vec2D(0.0, 0.0),
                tr size outerRadius,
                tr size innerRadius,
                fromRadian,
                toRadian,
                tr size padding,
                tr size padding,
                argb,
            ),
        )
    }
}
