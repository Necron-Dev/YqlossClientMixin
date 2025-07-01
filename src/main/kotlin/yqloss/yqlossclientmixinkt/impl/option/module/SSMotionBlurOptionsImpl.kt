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

import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.Header
import cc.polyfrost.oneconfig.config.annotations.Slider
import yqloss.yqlossclientmixinkt.impl.option.OptionsImpl
import yqloss.yqlossclientmixinkt.impl.option.adapter.Extract
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerAtOwnRisk
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerLegit
import yqloss.yqlossclientmixinkt.module.ssmotionblur.AlphaFunction
import yqloss.yqlossclientmixinkt.module.ssmotionblur.INFO_SS_MOTION_BLUR
import yqloss.yqlossclientmixinkt.module.ssmotionblur.SSMotionBlurOptions

class SSMotionBlurOptionsImpl :
    OptionsImpl(INFO_SS_MOTION_BLUR),
    SSMotionBlurOptions {
    @Transient
    @Extract
    val disclaimer = DisclaimerAtOwnRisk()

    @Transient
    @Extract
    val legit = DisclaimerLegit()

    @Transient
    @Header(
        text = "{module.ss_motion_blur.config.header.module}",
        size = 2,
    )
    val headerModule = false

    @Slider(
        name = "{module.ss_motion_blur.config.option.strength.text}",
        description = "{module.ss_motion_blur.config.option.strength.description}",
        min = 0.0F,
        max = 100.0F,
    )
    var strengthOption = 50.0F

    @Dropdown(
        name = "{module.ss_motion_blur.config.option.alpha_function.text}",
        description = "{module.ss_motion_blur.config.option.alpha_function.description}",
        options = [
            "{module.ss_motion_blur.config.option.alpha_function.options.0}",
            "{module.ss_motion_blur.config.option.alpha_function.options.1}",
            "{module.ss_motion_blur.config.option.alpha_function.options.2}",
        ],
    )
    var alphaFunctionOption = 2

    override val strength get() = strengthOption / 100.0
    override val alphaFunction get() = AlphaFunction.entries[alphaFunctionOption]
}
