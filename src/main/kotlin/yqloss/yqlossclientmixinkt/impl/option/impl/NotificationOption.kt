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
import yqloss.yqlossclientmixinkt.impl.option.adapter.Extract
import yqloss.yqlossclientmixinkt.module.option.YCNotificationOption

class NotificationOption : YCNotificationOption {
    @Switch(
        name = "{config.notification.notification.option.enabled.text}",
        description = "{config.notification.notification.option.enabled.description}",
        size = 2,
    )
    var enabledOption = false

    @Extract
    var logOption = LogOption()

    @Extract
    var printChatOption = PrintChatOption()

    @Extract
    var titleOption = TitleOption()

    @Extract
    var actionBarOption = ActionBarOption()

    @Extract
    var soundOption = SoundOption()

    @Extract
    var sendMessageOption = SendMessageOption()

    override val enabled by ::enabledOption
    override val log by ::logOption
    override val printChat by ::printChatOption
    override val title by ::titleOption
    override val actionBar by ::actionBarOption
    override val sound by ::soundOption
    override val sendMessage by ::sendMessageOption
}
