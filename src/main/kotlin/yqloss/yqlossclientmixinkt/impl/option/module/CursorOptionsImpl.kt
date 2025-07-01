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
        text = "{module.cursor.config.header.module}",
        size = 2,
    )
    val headerModule = false

    @Transient
    @Header(
        text = "{module.cursor.config.continuous.header.module}",
        size = 2,
    )
    val headerContinuous = false

    class Continuous {
        @Switch(
            name = "{module.cursor.config.continuous.option.enabled.text}",
            description = "{module.cursor.config.continuous.option.enabled.description}",
            size = 1,
        )
        val enabled = false

        @Color(
            name = "{module.cursor.config.continuous.option.color.text}",
            description = "{module.cursor.config.continuous.option.color.description}",
            size = 1,
        )
        val color = OneColor(-1)

        @Number(
            name = "{module.cursor.config.continuous.option.radius.text}",
            description = "{module.cursor.config.continuous.option.radius.description}",
            min = 0F,
            max = Float.MAX_VALUE,
            size = 1,
        )
        val radius = 1F

        @Number(
            name = "{module.cursor.config.continuous.option.bloom.text}",
            description = "{module.cursor.config.continuous.option.bloom.description}",
            min = 0F,
            max = Float.MAX_VALUE,
            size = 1,
        )
        val bloom = 3F

        @Number(
            name = "{module.cursor.config.continuous.option.duration.text}",
            description = "{module.cursor.config.continuous.option.duration.description}",
            min = 0F,
            max = Float.MAX_VALUE,
            size = 1,
        )
        val duration = 0F

        @Number(
            name = "{module.cursor.config.continuous.option.fade.text}",
            description = "{module.cursor.config.continuous.option.fade.description}",
            min = 0F,
            max = Float.MAX_VALUE,
            size = 1,
        )
        val fade = 0.1F

        @Number(
            name = "{module.cursor.config.continuous.option.radius_samples.text}",
            description = "{module.cursor.config.continuous.option.radius_samples.description}",
            min = 0F,
            max = Float.MAX_VALUE,
            size = 1,
        )
        val radiusSamples = 3

        @Number(
            name = "{module.cursor.config.continuous.option.time_samples.text}",
            description = "{module.cursor.config.continuous.option.time_samples.description}",
            min = 0F,
            max = Float.MAX_VALUE,
            size = 1,
        )
        val timeSamples = 20

        @Number(
            name = "{module.cursor.config.continuous.option.optimization.text}",
            description = "{module.cursor.config.continuous.option.optimization.description}",
            min = 0F,
            max = Float.MAX_VALUE,
            size = 1,
        )
        val optimization = 1F

        @Number(
            name = "{module.cursor.config.continuous.option.keep_samples_duration.text}",
            description = "{module.cursor.config.continuous.option.keep_samples_duration.description}",
            min = 0F,
            max = Float.MAX_VALUE,
            size = 1,
        )
        val keepSamples = 0.1F

        @Switch(
            name = "{module.cursor.config.continuous.option.hide_when_not_moving.text}",
            description = "{module.cursor.config.continuous.option.hide_when_not_moving.description}",
            size = 1,
        )
        val hideWhenNotMoving = false
    }

    @Extract
    var continuousOptions = Continuous()
}
