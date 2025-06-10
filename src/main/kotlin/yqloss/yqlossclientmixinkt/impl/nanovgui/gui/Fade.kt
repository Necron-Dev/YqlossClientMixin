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

import yqloss.yqlossclientmixinkt.impl.nanovgui.Transformation
import yqloss.yqlossclientmixinkt.impl.nanovgui.Widget
import yqloss.yqlossclientmixinkt.util.ExponentialSmooth

abstract class Fade<T>(
    private val initial: T,
) {
    var last = initial
    var curr = initial
    private val progressSmooth = ExponentialSmooth(0.0)

    open val smooth = true

    fun reset() {
        last = initial
        curr = initial
        progressSmooth.reset(0.0)
    }

    fun switch(value: T) {
        if (value != curr) {
            last = curr
            curr = value
            progressSmooth.set(1.0 - progressSmooth.value)
        }
    }

    open fun render(
        widgets: MutableList<Widget<*>>,
        tr: Transformation,
    ) {
        var progress = 1.0
        if (smooth) {
            progress = progressSmooth.approach(1.0, 0.5)
        } else {
            progressSmooth.reset(0.0)
        }
        renderSingle(widgets, tr, last, 1.0 - progress, true)
        renderSingle(widgets, tr, curr, progress, false)
    }

    abstract fun renderSingle(
        widgets: MutableList<Widget<*>>,
        tr: Transformation,
        info: T,
        progress: Double,
        isLast: Boolean,
    )
}
