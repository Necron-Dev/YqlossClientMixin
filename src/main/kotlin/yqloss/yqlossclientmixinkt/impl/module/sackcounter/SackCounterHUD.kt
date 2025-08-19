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

package yqloss.yqlossclientmixinkt.impl.module.sackcounter

import net.yqloss.uktil.extension.double
import net.yqloss.uktil.math.Vec2D
import yqloss.yqlossclientmixinkt.impl.module.YCModuleHUDBase
import yqloss.yqlossclientmixinkt.impl.nanovgui.Transformation
import yqloss.yqlossclientmixinkt.impl.nanovgui.Widget
import yqloss.yqlossclientmixinkt.impl.nanovgui.widget.TextWidget
import yqloss.yqlossclientmixinkt.impl.oneconfiginternal.fontSemiBold
import yqloss.yqlossclientmixinkt.impl.option.module.SackCounterOptionsImpl
import yqloss.yqlossclientmixinkt.impl.util.Colors
import yqloss.yqlossclientmixinkt.module.sackcounter.SackCounter
import kotlin.math.max

object SackCounterHUD : YCModuleHUDBase<SackCounterOptionsImpl, SackCounter>(SackCounter, { options.hud }) {
    private val entries get() = options.entries.split(',').map(String::trim).filter(String::isNotEmpty)

    override val width get() = options.width.double

    override val height get() = max(8.0, entries.size * 9.0 - 1.0)

    override val fadeOut = 3000000000L

    override val ensureShow get() = isHUDEnabled && (example || module.available)

    override fun draw(
        widgets: MutableList<Widget<*>>,
        box: Vec2D,
        tr: Transformation,
    ) {
        options.background.addTo(widgets, tr, box)

        entries.forEachIndexed { i, entry ->
            val count = module.getCount(entry)?.toString() ?: "???"
            widgets.add(
                TextWidget(
                    "$entry: $count",
                    tr pos Vec2D(0.0, i * 9.0),
                    Colors.GRAY[3].rgb,
                    tr size 8.0,
                    fontSemiBold,
                    Vec2D(0.0, 0.0),
                ),
            )
        }
    }
}
