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

package yqloss.yqlossclientmixinkt.impl.option.impl

import cc.polyfrost.oneconfig.config.annotations.Number
import cc.polyfrost.oneconfig.config.annotations.Switch
import net.yqloss.uktil.extension.double

class ScreenScaleOption {
    @Switch(
        name = "{config.screen_scale.option.override_screen_scale.text}",
        description = "{config.screen_scale.option.override_screen_scale.description}",
        size = 1,
    )
    var overrideEnabledOption: Boolean = false

    @Number(
        name = "{config.screen_scale.option.override_screen_scale_value.text}",
        description = "{config.screen_scale.option.override_screen_scale_value.description}",
        min = 0.125F,
        max = Float.MAX_VALUE,
        size = 1,
    )
    var scaleOption: Float = 1.0F

    val overrideEnabled by ::overrideEnabledOption
    val scale get() = scaleOption.double

    val nullableScale get() = scale.takeIf { overrideEnabled }
}
