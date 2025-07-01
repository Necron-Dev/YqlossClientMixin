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

import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.annotations.Text
import yqloss.yqlossclientmixinkt.module.option.YCPrintChatOption

class PrintChatOption : YCPrintChatOption {
    @Switch(
        name = "{config.notification.print_chat.option.enabled.text}",
        description = "{config.notification.print_chat.option.enabled.description}",
        size = 1,
    )
    var enabledOption = false

    @Text(
        name = "{config.notification.print_chat.option.text.text}",
        description = "{config.notification.print_chat.option.text.description}",
        size = 1,
    )
    var textOption = ""

    override val enabled by ::enabledOption
    override val text by ::textOption
}
