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

package yqloss.yqlossclientmixinkt.impl.nanovgui.widget

import net.yqloss.uktil.math.Vec2D
import net.yqloss.uktil.math.lerp

fun progressBarWidget(
    progress: Double,
    pos1: Vec2D,
    pos2: Vec2D,
    colorProgress: Int,
    colorBackground: Int,
) = ListWidget(
    RoundedRectWidget(
        pos1,
        pos2,
        colorBackground,
        (pos2 - pos1).y / 2.0,
    ),
    RoundedRectWidget(
        pos1,
        pos2.copy(x = lerp(pos1.x + (pos2 - pos1).y, pos2.x, progress)),
        colorProgress,
        (pos2 - pos1).y / 2.0,
    ),
)
