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

package yqloss.yqlossclientmixinkt.impl.option.language

import net.yqloss.uktil.accessor.getValue
import net.yqloss.uktil.accessor.refs.lateVal
import net.yqloss.uktil.accessor.setValue
import yqloss.yqlossclientmixinkt.YC
import yqloss.yqlossclientmixinkt.module.YCModuleBase
import yqloss.yqlossclientmixinkt.module.moduleInfo
import yqloss.yqlossclientmixinkt.module.option.YCModuleOptions

const val FALLBACK_LANGUAGE = "en_US"

var globalLanguage: String by lateVal()

object LanguageManager : YCModuleBase<YCModuleOptions>(moduleInfo("language_manager", "Language Manager")) {
    fun translate(string: String): String {
        string.startsWith('{') && string.endsWith('}') || return string
        val translationKey = string.substring(1..<string.length - 1)
        LanguageEvent.Translate(globalLanguage, FALLBACK_LANGUAGE, translationKey)
            .also(YC.eventDispatcher)
            .apply {
                if (!canceled) {
                    logger.warn("failed to translate string $string")
                }
                return mutableString
            }
    }
}
