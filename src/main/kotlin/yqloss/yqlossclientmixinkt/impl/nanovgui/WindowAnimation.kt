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

package yqloss.yqlossclientmixinkt.impl.nanovgui

import net.yqloss.uktil.math.Vec2D
import yqloss.yqlossclientmixinkt.util.ExponentialSmooth

class WindowAnimation {
    private var lastShow = false
    private var smoothAlpha = ExponentialSmooth(0.0)
    private var smoothScale = ExponentialSmooth(0.0)
    private var box = Vec2D(0.0, 0.0)
    private var tr = Transformation()
    private var lastShowFadeOut = false
    private var lastSwitch: Long? = null

    private fun doUpdate(show: Boolean): Boolean {
        if (show) {
            if (!lastShow) {
                smoothAlpha.set(0.1)
                smoothScale.set(0.9)
            }
            if (smoothAlpha.approach(1.5, 0.5) > 0.9999) {
                smoothAlpha.set(1.0)
            }
            if (smoothScale.approach(1.1, 0.5) > 0.9999) {
                smoothScale.set(1.0)
            }
        } else {
            if (lastShow) {
                smoothAlpha.set(1.0)
                smoothScale.set(1.0)
            }
            smoothAlpha.approach(-0.5, 0.5)
            smoothScale.approach(0.8, 0.5)
        }
        lastShow = show
        return show
    }

    fun update(
        show: Boolean,
        box: Vec2D,
        tr: Transformation,
        fadeOut: Long = 0,
    ): Boolean {
        val time = System.nanoTime()
        var toUpdate = true
        this.box = box
        this.tr = tr
        if (!show && fadeOut > 0) {
            if (lastShowFadeOut) {
                lastSwitch = time
                toUpdate = false
            } else if (lastSwitch?.let { time - it < fadeOut } == true) {
                toUpdate = false
            }
        }
        lastShowFadeOut = show
        return toUpdate and doUpdate(show || !toUpdate)
    }

    fun mapWidgets(
        widgets: List<Widget<*>>,
        eventWidgets: MutableList<Widget<*>>,
        fadeOutOrigin: Vec2D,
    ) {
        if (smoothAlpha.value > 0.1) {
            eventWidgets.addAll(
                widgets.map {
                    it
                        .alphaScale(smoothAlpha.value)
                        .scale(smoothScale.value, fadeOutOrigin)
                },
            )
        }
    }

    fun setShow(show: Boolean) {
        if (show) {
            smoothAlpha.set(1.0)
            smoothScale.set(1.0)
        } else {
            smoothAlpha.set(0.0)
            smoothScale.set(0.9)
        }
        lastSwitch = null
        lastShow = show
        lastShowFadeOut = show
    }
}
