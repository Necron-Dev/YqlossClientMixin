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

package yqloss.yqlossclientmixinkt.util

import kotlin.math.pow

class ExponentialSmooth(
    valueIn: Double,
) {
    var value = valueIn
        private set

    private var last: Long? = null

    fun approach(
        target: Double,
        speed: Double,
    ): Double {
        val time = System.nanoTime()
        val diff = time - (last ?: time)
        last = time
        value = target - (target - value) * (1.0 - speed).pow(diff / 50000000.0)
        return value
    }

    fun approachOrSet(
        target: Double,
        speed: Double,
    ): Double {
        val time = System.nanoTime()
        val diff = time - (last ?: time)
        value = if (last === null) {
            target
        } else {
            target - (target - value) * (1.0 - speed).pow(diff / 50000000.0)
        }
        last = time
        return value
    }

    fun set(target: Double) {
        value = target
        last = System.nanoTime()
    }

    fun reset(target: Double) {
        value = target
        last = null
    }
}
