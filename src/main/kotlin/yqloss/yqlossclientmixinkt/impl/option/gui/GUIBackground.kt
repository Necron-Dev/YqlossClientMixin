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

package yqloss.yqlossclientmixinkt.impl.option.gui

import cc.polyfrost.oneconfig.config.annotations.Color
import cc.polyfrost.oneconfig.config.annotations.Number
import cc.polyfrost.oneconfig.config.annotations.Switch
import net.yqloss.uktil.extension.double
import net.yqloss.uktil.math.Vec2D
import yqloss.yqlossclientmixinkt.impl.nanovgui.Transformation
import yqloss.yqlossclientmixinkt.impl.nanovgui.Widget
import yqloss.yqlossclientmixinkt.impl.nanovgui.widget.backgroundWidget
import yqloss.yqlossclientmixinkt.impl.util.Colors

class GUIBackground {
    @Switch(
        name = "Show Background",
        size = 2,
    )
    var enabledOption = true

    @Color(
        name = "Background Color",
        size = 2,
    )
    var backgroundColorOption = Colors.GRAY[9]

    @Number(
        name = "Rounded Corner Radius",
        min = 0.0F,
        max = Float.MAX_VALUE,
        size = 1,
    )
    var radiusOption = 6.0F

    @Number(
        name = "Shadow Blur",
        min = 0.0F,
        max = Float.MAX_VALUE,
        size = 1,
    )
    var shadowBlur = 2.0F

    @Number(
        name = "X Padding",
        min = 0.0F,
        max = Float.MAX_VALUE,
        size = 1,
    )
    var paddingXOption = 6.0F

    @Number(
        name = "Y Padding",
        min = 0.0F,
        max = Float.MAX_VALUE,
        size = 1,
    )
    var paddingYOption = 6.0F

    fun addTo(
        widgets: MutableList<Widget<*>>,
        tr: Transformation,
        size: Vec2D,
    ) {
        enabledOption || return
        widgets.run {
            add(
                backgroundWidget(
                    tr pos Vec2D(0.0, 0.0),
                    tr size size,
                    tr size Vec2D(paddingXOption, paddingYOption),
                    backgroundColorOption.rgb,
                    tr size radiusOption.double,
                    tr size shadowBlur.double,
                ),
            )
        }
    }
}
