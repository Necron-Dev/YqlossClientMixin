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

import net.minecraft.util.ResourceLocation
import yqloss.yqlossclientmixinkt.YC
import yqloss.yqlossclientmixinkt.api.YCTemplate
import yqloss.yqlossclientmixinkt.api.format
import yqloss.yqlossclientmixinkt.module.inWorld
import yqloss.yqlossclientmixinkt.util.CustomSound
import yqloss.yqlossclientmixinkt.util.MC

interface YCSoundOption {
    val enabled: Boolean
    val name: String
    val volume: Double
    val pitch: Double
}

inline operator fun YCSoundOption.invoke(placeholder: YCTemplate.() -> Unit) {
    enabled && inWorld || return
    MC.soundHandler.playSound(
        CustomSound(
            ResourceLocation(YC.api.format(name, placeholder)),
            volume,
            pitch,
        ),
    )
}
