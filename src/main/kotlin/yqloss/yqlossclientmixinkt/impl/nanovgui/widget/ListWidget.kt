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
import yqloss.yqlossclientmixinkt.impl.nanovgui.NanoVGUIContext
import yqloss.yqlossclientmixinkt.impl.nanovgui.Widget

class ListWidget(
    private val list: MutableList<Widget<*>> = mutableListOf(),
) : Widget<ListWidget> {
    constructor(vararg widgets: Widget<*>) : this(widgets.toMutableList())

    override fun draw(context: NanoVGUIContext) {
        list.forEach { it.draw(context) }
    }

    override fun alphaScale(alpha: Double) = ListWidget(list.map { it.alphaScale(alpha) }.toMutableList())

    override fun scale(
        scale: Double,
        origin: Vec2D,
    ) = ListWidget(list.map { it.scale(scale, origin) }.toMutableList())
}
