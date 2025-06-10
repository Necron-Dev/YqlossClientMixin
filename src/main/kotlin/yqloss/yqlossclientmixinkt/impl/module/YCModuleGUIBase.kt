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

package yqloss.yqlossclientmixinkt.impl.module

import net.yqloss.uktil.event.EventRegistry
import net.yqloss.uktil.math.Vec2D
import yqloss.yqlossclientmixinkt.impl.nanovgui.Transformation
import yqloss.yqlossclientmixinkt.impl.nanovgui.Widget
import yqloss.yqlossclientmixinkt.impl.nanovgui.WindowAnimation
import yqloss.yqlossclientmixinkt.impl.option.OptionsImpl
import yqloss.yqlossclientmixinkt.module.YCModule
import yqloss.yqlossclientmixinkt.module.option.YCModuleOptions

abstract class YCModuleGUIBase<TO, TM : YCModule<in TO>>(
    module: TM,
) : YCModuleImplBase<TO, TM>(module) where TO : YCModuleOptions, TO : OptionsImpl {
    abstract val width: Double
    abstract val height: Double
    open val fadeOut = 0L

    abstract val ensureShow: Boolean

    abstract fun draw(
        widgets: MutableList<Widget<*>>,
        box: Vec2D,
        tr: Transformation,
    )

    open fun reset() {}

    open val scaledWidth get() = width
    open val scaledHeight get() = height
    open val size get() = Vec2D(width, height)
    abstract val transformation: Transformation
    open var widgets = mutableListOf<Widget<*>>()
    open val animation = WindowAnimation()

    open fun getFadeOutOrigin(tr: Transformation) = tr pos size / 2.0

    open fun redraw(
        box: Vec2D,
        tr: Transformation,
    ) {
        widgets.clear()
        draw(widgets, box, tr)
    }

    open fun onRender(eventWidgets: MutableList<Widget<*>>) {
        val show = ensureShow
        val box = size
        val tr = transformation
        if (animation.update(show, box, tr, fadeOut)) {
            redraw(box, tr)
        }
        animation.mapWidgets(widgets, eventWidgets, getFadeOutOrigin(tr))
        if (!ensureShow) {
            reset()
        }
    }

    override val registerEvents: EventRegistry.() -> Unit = {
        super.registerEvents(this)
    }
}
