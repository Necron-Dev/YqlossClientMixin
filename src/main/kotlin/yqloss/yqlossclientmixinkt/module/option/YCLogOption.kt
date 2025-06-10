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

package yqloss.yqlossclientmixinkt.module.option

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Logger
import yqloss.yqlossclientmixinkt.YC
import yqloss.yqlossclientmixinkt.api.YCTemplate
import yqloss.yqlossclientmixinkt.api.format

interface YCLogOption {
    val enabled: Boolean
    val level: Int
    val text: String
}

inline operator fun YCLogOption.invoke(
    logger: Logger,
    placeholder: YCTemplate.() -> Unit,
) {
    enabled || return
    logger.log(
        when (level) {
            0 -> Level.FATAL
            1 -> Level.ERROR
            2 -> Level.WARN
            3 -> Level.INFO
            4 -> Level.DEBUG
            else -> Level.TRACE
        },
        YC.api.format(text, placeholder),
    )
}
