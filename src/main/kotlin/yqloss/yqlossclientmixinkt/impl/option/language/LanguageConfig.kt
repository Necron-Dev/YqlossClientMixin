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

import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.Header
import cc.polyfrost.oneconfig.config.annotations.Info
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.config.elements.SubConfig

private val LANGUAGE_LIST = listOf(
    "en_US",
    "zh_CN",
)

class LanguageConfig : SubConfig("Language 语言", "yqlossclient-language.json") {
    @Transient
    @Header(
        text = "Language 语言",
        size = 2,
    )
    val header = false

    @Transient
    @Info(
        type = InfoType.WARNING,
        text = "The language option takes effect after restarting the game!",
        size = 2,
    )
    val infoEnglish = false

    @Transient
    @Info(
        type = InfoType.WARNING,
        text = "设置的语言将在重启游戏后生效！",
        size = 2,
    )
    val infoChinese = false

    @Dropdown(
        name = "Language 语言",
        options = [
            "en_US English 英语",
            "zh_CN Simplified Chinese 简体中文",
        ],
    )
    val languageOption = 0

    val language get() = LANGUAGE_LIST[languageOption]

    override fun initialize() {
        super.initialize()
        globalLanguage = language
    }
}
