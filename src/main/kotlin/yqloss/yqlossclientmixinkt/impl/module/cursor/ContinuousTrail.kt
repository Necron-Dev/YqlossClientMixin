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

package yqloss.yqlossclientmixinkt.impl.module.cursor

import net.yqloss.uktil.extension.double
import net.yqloss.uktil.extension.long
import net.yqloss.uktil.extension.type.prepend
import net.yqloss.uktil.math.Vec2D
import net.yqloss.uktil.math.lerp
import yqloss.yqlossclientmixinkt.impl.nanovgui.GUIEvent
import yqloss.yqlossclientmixinkt.impl.option.module.CursorOptionsImpl

object ContinuousTrail : Trail<CursorOptionsImpl.Continuous> {
    data class SamplePoint(
        val position: Vec2D,
        val time: Long,
        val rendered: Boolean,
    )

    private val samplePoints = ArrayDeque<SamplePoint>()

    private val renderedPoints = ArrayDeque<SamplePoint>()

    private var lastMouse: Vec2D? = null

    private var lastMouseMoveTime = 0L

    private fun ArrayDeque<SamplePoint>.removeDead(
        time: Long,
        alive: Long,
    ) {
        while (isNotEmpty()) {
            if (time - last().time >= alive) {
                removeLast()
            } else {
                break
            }
        }
    }

    override fun clear() {
        samplePoints.clear()
        renderedPoints.clear()
    }

    override fun render(
        event: GUIEvent.Screen.Post,
        mouse: Vec2D,
        time: Long,
        lastTime: Long,
        options: CursorOptionsImpl.Continuous,
    ) {
        val lastMouse = lastMouse.also {
            if (it != mouse) lastMouseMoveTime = time
            lastMouse = mouse
        }

        if (!options.enabled) {
            clear()
            return
        }

        val alive = ((options.duration + options.fade + options.keepSamples) * 1e9).long

        samplePoints.removeDead(time, alive)
        renderedPoints.removeDead(time, alive)

        if (renderedPoints.isEmpty()) {
            val sample = SamplePoint(mouse, time, true)
            samplePoints += sample
            renderedPoints += sample
        } else if (time != lastTime) {
            val firstRendered = renderedPoints.first()
            val direction = firstRendered.position - mouse
            val unit = direction / direction.length
            var renderLastPoint = false
            samplePoints.firstOrNull {
                if (it === firstRendered) {
                    true
                } else {
                    val diff = it.position - mouse
                    val length = diff.length
                    val dot = diff * unit
                    val distanceSquared = length * length - dot * dot
                    if (dot < 0 || distanceSquared > options.optimization) {
                        renderLastPoint = true
                        true
                    } else {
                        false
                    }
                }
            }
            if (renderLastPoint) {
                samplePoints[0] = samplePoints[0].copy(rendered = true)
                renderedPoints.addFirst(samplePoints[0])
            }
            samplePoints.addFirst(SamplePoint(mouse, time, false))
        }

        if (renderedPoints.isEmpty()) return

        event.widgets +=
            ContinuousTrailWidget(
                if (renderedPoints[0] === samplePoints[0]) {
                    ArrayDeque(renderedPoints)
                } else if (options.hideWhenNotMoving && mouse == lastMouse) {
                    ArrayDeque(renderedPoints prepend samplePoints[0].copy(time = lastMouseMoveTime))
                } else {
                    ArrayDeque(renderedPoints prepend samplePoints[0])
                },
                options.radius.double,
                options.bloom.double,
                options.duration.double,
                options.fade.double,
                options.color.rgb,
                options.color.alpha / 255.0,
                options.timeSamples,
                options.radiusSamples,
            ) { time, trailT ->
                var last = samplePoints[0]
                samplePoints.firstOrNull {
                    if (time - it.time <= trailT) {
                        last = it
                        false
                    } else {
                        val position =
                            lerp(
                                last.position,
                                it.position,
                                (time - last.time - trailT).double / (it.time - last.time),
                            )
                        return@ContinuousTrailWidget position
                    }
                }
                last.position
            }
    }
}
