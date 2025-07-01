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
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.annotations.Text
import yqloss.yqlossclientmixinkt.module.option.YCActionBarOption

class ActionBarOption : YCActionBarOption {
    @Switch(
        name = "{config.notification.actionbar.option.enabled.text}",
        description = "{config.notification.actionbar.option.enabled.description}",
        size = 2,
    )
    var enabledOption = false

    @Text(
        name = "{config.notification.actionbar.option.text.text}",
        description = "{config.notification.actionbar.option.text.description}",
        size = 1,
    )
    var textOption = ""

    @Checkbox(
        name = "{config.notification.actionbar.option.chroma.text}",
        description = "{config.notification.actionbar.option.chroma.description}",
        size = 1,
    )
    var chromaOption = false

    override val enabled by ::enabledOption
    override val text by ::textOption
    override val chroma by ::chromaOption
}
