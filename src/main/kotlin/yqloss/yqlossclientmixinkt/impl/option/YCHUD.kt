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

package yqloss.yqlossclientmixinkt.impl.option

import cc.polyfrost.oneconfig.hud.Hud
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import net.yqloss.uktil.event.Event
import net.yqloss.uktil.extension.double
import net.yqloss.uktil.extension.float
import net.yqloss.uktil.math.Vec2D
import yqloss.yqlossclientmixinkt.YC

class YCHUD(
    enabled: Boolean = false,
    x: Double = 0.0,
    y: Double = 0.0,
    positionAlignment: Int = 0,
    scale: Double = 1.0,
) : Hud(enabled, x.float, y.float, positionAlignment, scale.float) {
    @Transient
    var renderX = 0.0
        private set

    @Transient
    var renderY = 0.0
        private set

    @Transient
    var isExample = false
        private set

    val renderScale get() = scale.double
    val renderPos get() = Vec2D(renderX, renderY)

    override fun draw(
        matrices: UMatrixStack,
        x: Float,
        y: Float,
        scale: Float,
        example: Boolean,
    ) {
        renderX = x.double
        renderY = y.double
        isExample = example
    }

    override fun getWidth(
        scale: Float,
        example: Boolean,
    ) = GetWidthEvent(this).also(YC.eventDispatcher).width.float

    override fun getHeight(
        scale: Float,
        example: Boolean,
    ) = GetHeightEvent(this).also(YC.eventDispatcher).height.float

    data class GetWidthEvent(
        val hud: Hud,
        var width: Double = 0.0,
    ) : Event

    data class GetHeightEvent(
        val hud: Hud,
        var height: Double = 0.0,
    ) : Event
}
