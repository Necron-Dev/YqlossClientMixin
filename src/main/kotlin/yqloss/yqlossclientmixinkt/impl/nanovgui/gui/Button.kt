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

import net.yqloss.uktil.math.*
import org.lwjgl.input.Mouse
import yqloss.yqlossclientmixinkt.impl.nanovgui.Transformation
import yqloss.yqlossclientmixinkt.impl.nanovgui.Widget
import yqloss.yqlossclientmixinkt.impl.nanovgui.widget.RoundedRectWidget
import yqloss.yqlossclientmixinkt.impl.util.Colors
import yqloss.yqlossclientmixinkt.util.ExponentialSmooth
import yqloss.yqlossclientmixinkt.util.mousePosition
import kotlin.math.min

abstract class Button<T>(
    var info: T,
) {
    private val rSmooth = ExponentialSmooth(0.0)
    private val gSmooth = ExponentialSmooth(0.0)
    private val bSmooth = ExponentialSmooth(0.0)
    private val aSmooth = ExponentialSmooth(0.0)
    private val hoverLSmooth = ExponentialSmooth(0.0)
    private val sizeSmooth = ExponentialSmooth(1.0)

    open fun render(
        widgets: MutableList<Widget<*>>,
        tr: Transformation,
    ) {
        val hovered = isHovered(tr)
        var size = getSize(hovered)
        if (smooth) {
            size = sizeSmooth.approach(size, 0.5)
        }
        val ttr = tr translate collisionSize / 2.0 scale size
        renderBackground(widgets, ttr, hovered)
        renderIcon(widgets, ttr, hovered)
    }

    open val smooth = true

    open val cornerRadius = 4.0

    abstract val collisionSize: Vec2D

    open val buttons = booleanArrayOf(false, false, false)

    open val pressed = booleanArrayOf(false, false, false)

    open fun isHovered(tr: Transformation): Boolean {
        return !tr pos mousePosition in Vec2D(0.0, 0.0) areaTo collisionSize
    }

    open fun getColor(hovered: Boolean) = Colors.BLUE[6].rgb

    open fun getRenderSize(hovered: Boolean) = collisionSize

    open fun getSize(hovered: Boolean) = if (hovered && isAnyDown()) 0.9 else 1.0

    open fun getLightnessModifier(hovered: Boolean) = if (hovered) {
        if (isAnyDown()) {
            0.05
        } else {
            0.1
        }
    } else {
        0.0
    }

    open fun getActualColor(hovered: Boolean): Int {
        var (r, g, b, a) = convertARGBToDoubleArray(getColor(hovered))
        if (smooth) {
            r = rSmooth.approachOrSet(r, 0.5)
            g = gSmooth.approachOrSet(g, 0.5)
            b = bSmooth.approachOrSet(b, 0.5)
            a = aSmooth.approachOrSet(a, 0.5)
        }
        var (h, s, l) = convertRGBToHSL(r, g, b)
        var ld = getLightnessModifier(hovered)
        if (smooth) {
            ld = hoverLSmooth.approach(ld, 0.5)
        }
        l = min(1.0, l + ld)
        return convertDoubleArrayToARGB(*convertHSLToRGB(h, s, l, a))
    }

    open fun renderBackground(
        widgets: MutableList<Widget<*>>,
        tr: Transformation,
        hovered: Boolean,
    ) {
        val argb = getActualColor(hovered)
        val renderOffset = getRenderSize(hovered) / 2.0
        widgets.add(
            RoundedRectWidget(
                tr pos -renderOffset,
                tr pos renderOffset,
                argb,
                tr size cornerRadius,
            ),
        )
    }

    open fun renderIcon(
        widgets: MutableList<Widget<*>>,
        tr: Transformation,
        hovered: Boolean,
    ) {
    }

    open fun checkMouse(tr: Transformation) {
        val hovered = isHovered(tr)
        buttons.forEachIndexed { i, last ->
            val clicked = Mouse.isButtonDown(i)
            if (hovered && !last && clicked) {
                onMouseDown(i)
                pressed[i] = true
            } else if (pressed[i] && !clicked) {
                onMouseUp(i)
                pressed[i] = false
            }
            buttons[i] = clicked
        }
    }

    open fun onMouseDown(button: Int) {}

    open fun onMouseUp(button: Int) {}

    protected fun isAnyDown(vararg buttons: Int = intArrayOf(0, 1, 2)) = buttons.any(Mouse::isButtonDown)
}
