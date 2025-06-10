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

import yqloss.yqlossclientmixinkt.YC
import yqloss.yqlossclientmixinkt.api.YCTemplate
import yqloss.yqlossclientmixinkt.api.format
import yqloss.yqlossclientmixinkt.module.inWorld
import yqloss.yqlossclientmixinkt.util.MC

interface YCActionBarOption {
    val enabled: Boolean
    val chroma: Boolean
    val text: String
}

inline operator fun YCActionBarOption.invoke(placeholder: YCTemplate.() -> Unit) {
    enabled && inWorld || return
    MC.ingameGUI.setRecordPlaying(
        YC.api.format(text, placeholder),
        chroma,
    )
}
