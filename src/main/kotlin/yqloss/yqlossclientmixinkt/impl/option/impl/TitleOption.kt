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

import cc.polyfrost.oneconfig.config.annotations.Checkbox
import cc.polyfrost.oneconfig.config.annotations.Number
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.annotations.Text
import yqloss.yqlossclientmixinkt.module.option.YCTitleOption

class TitleOption : YCTitleOption {
    @Switch(
        name = "{config.notification.title.option.enabled.text}",
        description = "{config.notification.title.option.enabled.description}",
        size = 1,
    )
    var enabledOption = false

    @Text(
        name = "{config.notification.title.option.text.text}",
        description = "{config.notification.title.option.text.description}",
        size = 1,
    )
    var textOption = ""

    @Checkbox(
        name = "{config.notification.title.option.set_subtitle.text}",
        description = "{config.notification.title.option.set_subtitle.description}",
        size = 1,
    )
    var setSubtitleOption = true

    @Text(
        name = "{config.notification.title.option.subtitle.text}",
        description = "{config.notification.title.option.subtitle.description}",
        size = 1,
    )
    var subtitleOption = ""

    @Checkbox(
        name = "{config.notification.title.option.set_fade_in_display_fade_out_duration.text}",
        description = "{config.notification.title.option.set_fade_in_display_fade_out_duration.description}",
        size = 1,
    )
    var setTimeOption = true

    @Number(
        name = "{config.notification.title.option.display_duration_ticks.text}",
        description = "{config.notification.title.option.display_duration_ticks.description}",
        min = 0.0F,
        max = Float.MAX_VALUE,
        step = 1,
        size = 1,
    )
    var stayOption = 60

    @Number(
        name = "{config.notification.title.option.fade_in_duration_ticks.text}",
        description = "{config.notification.title.option.fade_in_duration_ticks.description}",
        min = 0.0F,
        max = Float.MAX_VALUE,
        step = 1,
        size = 1,
    )
    var fadeInOption = 0

    @Number(
        name = "{config.notification.title.option.fade_out_duration_ticks.text}",
        description = "{config.notification.title.option.fade_out_duration_ticks.description}",
        min = 0.0F,
        max = Float.MAX_VALUE,
        step = 1,
        size = 1,
    )
    var fadeOutOption = 0

    override val enabled by ::enabledOption
    override val text by ::textOption
    override val setSubtitle by ::setSubtitleOption
    override val subtitle by ::subtitleOption
    override val setTime by ::setTimeOption
    override val stay by ::stayOption
    override val fadeIn by ::fadeInOption
    override val fadeOut by ::fadeOutOption
}
