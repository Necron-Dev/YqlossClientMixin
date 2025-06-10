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

import net.minecraft.client.gui.ScaledResolution
import net.yqloss.uktil.extension.double
import net.yqloss.uktil.math.Vec2D
import yqloss.yqlossclientmixinkt.util.MC

data class Transformation(
    val offset: Vec2D = Vec2D(0.0, 0.0),
    val scale: Double = 1.0,
) {
    infix fun translate(vec: Vec2D) = Transformation(offset + vec * scale, scale)

    infix fun scale(factor: Double) = Transformation(offset, scale * factor)

    infix fun pos(vec: Vec2D) = offset + vec * scale

    infix fun size(num: Double) = num * scale

    infix fun size(vec: Vec2D) = vec * scale

    operator fun plus(vec: Vec2D) = translate(vec)

    operator fun minus(vec: Vec2D) = translate(-vec)

    operator fun times(factor: Double) = scale(factor)

    operator fun div(factor: Double) = scale(1.0 / factor)

    operator fun not() = Transformation(-offset / scale, 1.0 / scale)

    fun scaleMC(scaleOverride: Double? = null) = scale(scaleOverride ?: ScaledResolution(MC).scaleFactor.double)

    infix fun translateScreen(vec: Vec2D) = translate(Vec2D(MC.displayWidth * vec.x, MC.displayHeight * vec.y))
}
