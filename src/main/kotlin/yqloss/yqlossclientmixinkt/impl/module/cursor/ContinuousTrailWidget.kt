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

import yqloss.yqlossclientmixinkt.impl.nanovgui.NanoVGUIContext
import yqloss.yqlossclientmixinkt.impl.nanovgui.Widget
import yqloss.yqlossclientmixinkt.impl.oneconfiginternal.nvg
import yqloss.yqlossclientmixinkt.impl.util.alphaScale
import yqloss.yqlossclientmixinkt.util.extension.long
import yqloss.yqlossclientmixinkt.util.math.Vec2D
import yqloss.yqlossclientmixinkt.util.math.lerp

data class ContinuousTrailWidget(
    private val samplePoints: List<ContinuousTrail.SamplePoint>,
    private val radius: Double,
    private val bloom: Double,
    private val duration: Double,
    private val fade: Double,
    private val color: Int,
    private val alpha: Double,
    private val timeSamples: Int,
    private val radiusSamples: Int,
    private val mousePositionGetter: (Long, Long) -> Vec2D,
) : Widget<ContinuousTrailWidget> {
    override fun draw(context: NanoVGUIContext) {
    }

    override fun postDraw() {
        if (samplePoints.isEmpty()) return

        val time = System.nanoTime()
        val s = timeSamples * radiusSamples

        nvg.runInNoAAContext { vgNoAA ->
            repeat(timeSamples) { t ->
                repeat(radiusSamples) { r ->
                    val trailA = alpha * s / ((s - alpha * (r + 1) * t) * (s - alpha * (t + 1) * r))
                    val trailR = radius + (radiusSamples - r - 1) * bloom / radiusSamples
                    val trailT = ((duration + (timeSamples - t - 1) * fade / timeSamples) * 1e9).long
                    val trail =
                        iterator {
                            var firstRendered = false
                            samplePoints.firstOrNull {
                                if (time - it.time <= trailT) {
                                    yield(it.position.x to it.position.y)
                                    firstRendered = true
                                    false
                                } else {
                                    if (firstRendered) {
                                        val position = mousePositionGetter(time, trailT)
                                        yield(position.x to position.y)
                                    }
                                    true
                                }
                            }
                        }
                    nvg.drawLines(vgNoAA, trail, trailR, color, trailA)
                }
            }
        }
    }

    override fun alphaScale(alpha: Double) = copy(color = color alphaScale alpha)

    override fun scale(
        scale: Double,
        origin: Vec2D,
    ) = copy(
        samplePoints = samplePoints.map { it.copy(position = lerp(origin, it.position, scale)) },
        radius = radius * scale,
        bloom = bloom * scale,
    )
}
