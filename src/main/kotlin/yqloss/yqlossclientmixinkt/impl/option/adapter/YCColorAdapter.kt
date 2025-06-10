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

package yqloss.yqlossclientmixinkt.impl.option.adapter

import cc.polyfrost.oneconfig.config.core.OneColor
import net.yqloss.uktil.extension.int
import yqloss.yqlossclientmixinkt.module.option.YCColor
import yqloss.yqlossclientmixinkt.module.option.YCColorImpl

val OneColor.asYCColor: YCColor get() = YCColorImpl(red / 255.0, green / 255.0, blue / 255.0, alpha / 255.0)

val YCColor.asOneColor: OneColor get() = OneColor((r * 255).int, (g * 255).int, (b * 255).int, (a * 255).int)
