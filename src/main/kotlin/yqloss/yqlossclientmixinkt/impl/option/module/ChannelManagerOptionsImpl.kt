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
import cc.polyfrost.oneconfig.config.annotations.Number
import yqloss.yqlossclientmixinkt.impl.option.OptionsImpl
import yqloss.yqlossclientmixinkt.impl.option.adapter.Extract
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerAtOwnRisk
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerLegit
import yqloss.yqlossclientmixinkt.impl.option.impl.NotificationOption
import yqloss.yqlossclientmixinkt.module.channelmanager.ChannelManagerOptions
import yqloss.yqlossclientmixinkt.module.channelmanager.INFO_CHANNEL_MANAGER

class ChannelManagerOptionsImpl :
    OptionsImpl(INFO_CHANNEL_MANAGER),
    ChannelManagerOptions {
    @Transient
    @Extract
    val disclaimer = DisclaimerAtOwnRisk()

    @Transient
    @Extract
    val legit = DisclaimerLegit()

    @Transient
    @Header(
        text = "{module.channel_manager.config.header.module}",
        size = 2,
    )
    val headerModule = false

    @Number(
        name = "{module.channel_manager.config.option.block_size.text}",
        description = "{module.channel_manager.config.option.block_size.description}",
        min = 1F,
        max = 16F,
    )
    var blockSizeOption = 2

    @Extract
    var onSendMessageOption = NotificationOption().apply {
        enabledOption = true
        sendMessageOption.apply {
            enabledOption = true
            textOption = "/pc <message>"
            enableIntervalOption = true
            intervalOption = 20
            intervalPoolOption = "hypixelpc"
        }
    }

    override val blockSize by ::blockSizeOption
    override val onSendMessage by ::onSendMessageOption
}
