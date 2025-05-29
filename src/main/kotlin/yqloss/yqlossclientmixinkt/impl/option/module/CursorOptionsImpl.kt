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

package yqloss.yqlossclientmixinkt.impl.option.module

import cc.polyfrost.oneconfig.config.annotations.Color
import cc.polyfrost.oneconfig.config.annotations.Header
import cc.polyfrost.oneconfig.config.annotations.Number
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneColor
import yqloss.yqlossclientmixinkt.impl.option.OptionsImpl
import yqloss.yqlossclientmixinkt.impl.option.adapter.Extract
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerAtOwnRisk
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerLegit
import yqloss.yqlossclientmixinkt.module.cursor.CursorOptions
import yqloss.yqlossclientmixinkt.module.cursor.INFO_CURSOR

class CursorOptionsImpl :
    OptionsImpl(INFO_CURSOR),
    CursorOptions {
    @Transient
    @Extract
    val disclaimer = DisclaimerAtOwnRisk()

    @Transient
    @Extract
    val legit = DisclaimerLegit()

    @Transient
    @Header(
        text = "Cursor",
        size = 2,
    )
    val headerModule = false

    @Transient
    @Header(
        text = "Continuous Trail",
        size = 2,
    )
    val headerContinuous = false

    class Continuous {
        @Switch(
            name = "Enabled",
            size = 1,
        )
        val enabled = false

        @Color(
            name = "Color",
            size = 1,
        )
        val color = OneColor(-1)

        @Number(
            name = "Radius",
            min = 0F,
            max = Float.MAX_VALUE,
            size = 1,
        )
        val radius = 1F

        @Number(
            name = "Bloom",
            min = 0F,
            max = Float.MAX_VALUE,
            size = 1,
        )
        val bloom = 3F

        @Number(
            name = "Duration",
            min = 0F,
            max = Float.MAX_VALUE,
            size = 1,
        )
        val duration = 0F

        @Number(
            name = "Fade",
            min = 0F,
            max = Float.MAX_VALUE,
            size = 1,
        )
        val fade = 0.1F

        @Number(
            name = "Radius Samples",
            min = 0F,
            max = Float.MAX_VALUE,
            size = 1,
        )
        val radiusSamples = 3

        @Number(
            name = "Time Samples",
            min = 0F,
            max = Float.MAX_VALUE,
            size = 1,
        )
        val timeSamples = 20

        @Number(
            name = "Optimization",
            min = 0F,
            max = Float.MAX_VALUE,
            size = 1,
        )
        val optimization = 1F

        @Number(
            name = "Keep Samples Duration",
            min = 0F,
            max = Float.MAX_VALUE,
            size = 1,
        )
        val keepSamples = 0.1F

        @Switch(
            name = "Hide When Not Moving",
            size = 1,
        )
        val hideWhenNotMoving = false
    }

    @Extract
    var continuousOptions = Continuous()
}
