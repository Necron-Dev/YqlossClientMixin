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

import cc.polyfrost.oneconfig.config.annotations.HUD
import cc.polyfrost.oneconfig.config.annotations.Header
import cc.polyfrost.oneconfig.config.annotations.Number
import cc.polyfrost.oneconfig.config.annotations.Text
import yqloss.yqlossclientmixinkt.impl.option.OptionsImpl
import yqloss.yqlossclientmixinkt.impl.option.YCHUD
import yqloss.yqlossclientmixinkt.impl.option.adapter.Extract
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerAtOwnRisk
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerLegit
import yqloss.yqlossclientmixinkt.impl.option.gui.GUIBackground
import yqloss.yqlossclientmixinkt.module.sackcounter.INFO_SACK_COUNTER
import yqloss.yqlossclientmixinkt.module.sackcounter.SackCounterOptions

class SackCounterOptionsImpl :
    OptionsImpl(INFO_SACK_COUNTER),
    SackCounterOptions {
    @Transient
    @Extract
    val disclaimer = DisclaimerAtOwnRisk()

    @Transient
    @Extract
    val legit = DisclaimerLegit()

    @Transient
    @Header(
        text = "{module.sack_counter.config.header.module}",
        size = 2,
    )
    val headerModule = false

    @HUD(
        name = "{module.sack_counter.config.hud.sack_counter}",
    )
    var hud = YCHUD()

    @Extract
    var background = GUIBackground()

    @Number(
        name = "{module.sack_counter.config.option.width.text}",
        description = "{module.sack_counter.config.option.width.description}",
        min = 0.0F,
        max = Float.POSITIVE_INFINITY,
        size = 1,
    )
    var width = 200.0F

    @Text(
        name = "{module.sack_counter.config.option.entries.text}",
        description = "{module.sack_counter.config.option.entries.description}",
        size = 2,
    )
    var entries = ""
}
