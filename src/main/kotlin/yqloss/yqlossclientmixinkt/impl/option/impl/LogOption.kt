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

import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.annotations.Text
import yqloss.yqlossclientmixinkt.module.option.YCLogOption

class LogOption : YCLogOption {
    @Switch(
        name = "{config.notification.log.option.enabled.text}",
        description = "{config.notification.log.option.enabled.description}",
        size = 2,
    )
    var enabledOption = false

    @Text(
        name = "{config.notification.log.option.text.text}",
        description = "{config.notification.log.option.text.description}",
        size = 1,
    )
    var textOption = ""

    @Dropdown(
        name = "{config.notification.log.option.level.text}",
        description = "{config.notification.log.option.level.description}",
        options = [
            "{config.notification.log.option.level.options.0}",
            "{config.notification.log.option.level.options.1}",
            "{config.notification.log.option.level.options.2}",
            "{config.notification.log.option.level.options.3}",
            "{config.notification.log.option.level.options.4}",
            "{config.notification.log.option.level.options.5}",
        ],
        size = 1,
    )
    var levelOption = 3

    override val enabled by ::enabledOption
    override val text by ::textOption
    override val level by ::levelOption
}
