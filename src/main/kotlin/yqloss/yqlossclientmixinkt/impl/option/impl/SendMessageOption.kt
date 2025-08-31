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

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.annotations.Number
import yqloss.yqlossclientmixinkt.impl.option.adapter.Extract
import yqloss.yqlossclientmixinkt.module.option.SendMessagePool
import yqloss.yqlossclientmixinkt.module.option.YCSendMessageOption
import yqloss.yqlossclientmixinkt.util.printChatTranslated

class SendMessageOption : YCSendMessageOption {
    @Switch(
        name = "{config.notification.send_message.option.enabled.text}",
        description = "{config.notification.send_message.option.enabled.description}",
        size = 1,
    )
    var enabledOption = false

    @Text(
        name = "{config.notification.send_message.option.text.text}",
        description = "{config.notification.send_message.option.text.description}",
        size = 1,
    )
    var textOption = ""

    @Checkbox(
        name = "{config.notification.send_message.option.enable_interval.text}",
        description = "{config.notification.send_message.option.enable_interval.description}",
        size = 1,
    )
    var enableIntervalOption = false

    @Number(
        name = "{config.notification.send_message.option.interval_since_last_message_ticks.text}",
        description = "{config.notification.send_message.option.interval_since_last_message_ticks.description}",
        min = 0.0F,
        max = Float.MAX_VALUE,
        step = 1,
        size = 1,
    )
    var intervalOption = 0

    @Text(
        name = "{config.notification.send_message.option.interval_pool.text}",
        description = "{config.notification.send_message.option.interval_pool.description}",
        size = 1,
    )
    var intervalPoolOption = "default"

    @Number(
        name = "{config.notification.send_message.option.max_pool_size.text}",
        description = "{config.notification.send_message.option.max_pool_size.description}",
        min = 0.0F,
        max = Float.MAX_VALUE,
        step = 1,
        size = 1,
    )
    var maxPoolSizeOption = 4194304

    override val enabled by ::enabledOption
    override val text by ::textOption
    override val enableInterval by ::enableIntervalOption
    override val interval by ::intervalOption
    override val intervalPool by ::intervalPoolOption
    override val maxPoolSize by ::maxPoolSizeOption

    @Transient
    @Extract
    val clearPool =
        @Button(
            name = "{config.notification.send_message.function.clear_this_pool.text}",
            text = "{config.notification.send_message.function.clear_this_pool.button}",
            description = "{config.notification.send_message.function.clear_this_pool.description}",
            size = 1,
        )
        {
            SendMessagePool.clear(intervalPool)
            printChatTranslated("{config.notification.send_message.message.clear_pool}") {
                this["pool"] = intervalPool
            }
        }

    @Transient
    @Extract
    val viewPoolSize =
        @Button(
            name = "{config.notification.send_message.function.print_pool_sizes.text}",
            text = "{config.notification.send_message.function.print_pool_sizes.button}",
            description = "{config.notification.send_message.function.print_pool_sizes.description}",
            size = 1,
        )
        {
            printChatTranslated("{config.notification.send_message.message.pool_size.header}")
            SendMessagePool.poolMap.forEach { (pool, list) ->
                if (list.isNotEmpty()) {
                    printChatTranslated("{config.notification.send_message.message.clear_pool}") {
                        this["pool"] = pool
                        this["size"] = list.size
                    }
                }
            }
            printChatTranslated("{config.notification.send_message.message.pool_size.footer}")
        }
}
