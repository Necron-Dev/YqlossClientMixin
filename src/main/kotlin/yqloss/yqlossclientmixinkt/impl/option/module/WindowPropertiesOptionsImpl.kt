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

import cc.polyfrost.oneconfig.config.annotations.Header
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.annotations.Text
import yqloss.yqlossclientmixinkt.impl.option.OptionsImpl
import yqloss.yqlossclientmixinkt.impl.option.adapter.Extract
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerAtOwnRisk
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerLegit
import yqloss.yqlossclientmixinkt.module.windowproperties.INFO_WINDOW_PROPERTIES
import yqloss.yqlossclientmixinkt.module.windowproperties.WindowPropertiesOptions

class WindowPropertiesOptionsImpl :
    OptionsImpl(INFO_WINDOW_PROPERTIES),
    WindowPropertiesOptions {
    @Transient
    @Extract
    val disclaimer = DisclaimerAtOwnRisk()

    @Transient
    @Extract
    val legit = DisclaimerLegit()

    @Transient
    @Header(
        text = "Window Properties",
        size = 2,
    )
    val headerModule = false

    @Switch(
        name = "Windowed Fullscreen",
        size = 1,
    )
    var windowedFullscreenOption = false

    @Switch(
        name = "Disable Fullscreen Optimization",
        size = 1,
    )
    var disableFullscreenOptimizationOption = false

    @Switch(
        name = "Borderless Window",
        size = 2,
    )
    var borderlessWindowOption = false

    @Switch(
        name = "Enable Custom Title",
        size = 2,
    )
    var enableCustomTitleOption = false

    @Text(
        name = "Custom Title",
        size = 2,
    )
    var customTitleOption = "Minecraft 1.8.9 [Yqloss Client]"

    @Transient
    @Header(
        text = "Debug",
        size = 2,
    )
    val headerDebug = false

    @Switch(
        name = "Half Fullscreen",
        size = 2,
    )
    var debugHalfFullscreenOption = false

    override val windowedFullscreen by ::windowedFullscreenOption
    override val disableFullscreenOptimization by ::disableFullscreenOptimizationOption
    override val borderlessWindow by ::borderlessWindowOption
    override val enableCustomTitle by ::enableCustomTitleOption
    override val customTitle by ::customTitleOption
    override val debugHalfFullscreen by ::debugHalfFullscreenOption
}
