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
import net.yqloss.uktil.event.register
import yqloss.yqlossclientmixinkt.impl.nanovgui.GUIEvent
import yqloss.yqlossclientmixinkt.impl.nanovgui.Transformation
import yqloss.yqlossclientmixinkt.impl.nanovgui.Widget
import yqloss.yqlossclientmixinkt.impl.option.OptionsImpl
import yqloss.yqlossclientmixinkt.impl.option.YCHUD
import yqloss.yqlossclientmixinkt.module.YCModule
import yqloss.yqlossclientmixinkt.module.enabled
import yqloss.yqlossclientmixinkt.module.option.YCModuleOptions

abstract class YCModuleHUDBase<TO, TM : YCModule<in TO>>(
    module: TM,
    protected val hudGetter: YCModuleHUDBase<TO, TM>.() -> YCHUD,
) : YCModuleGUIBase<TO, TM>(module) where TO : YCModuleOptions, TO : OptionsImpl {
    protected open val hud get() = hudGetter()
    protected open val example get() = hud.isExample
    override val scaledWidth get() = width * hud.scale
    override val scaledHeight get() = height * hud.scale
    override val transformation get() = Transformation().scaleMC() translate hud.renderPos scale hud.renderScale

    override fun onRender(eventWidgets: MutableList<Widget<*>>) {
        val show = ensureShow
        val box = size
        val tr = transformation
        if (example) {
            if (show) {
                redraw(box, tr)
            }
            eventWidgets.addAll(widgets)
        } else {
            if (animation.update(show, box, tr, fadeOut)) {
                redraw(box, tr)
            }
            animation.mapWidgets(widgets, eventWidgets, getFadeOutOrigin(tr))
        }
    }

    protected open val isHUDEnabled get() = enabled && hud.isEnabled

    override val registerEvents: EventRegistry.() -> Unit
        get() = {
            super.registerEvents(this)

            register<YCHUD.GetWidthEvent> { if (it.hud === hud) it.width = scaledWidth }

            register<YCHUD.GetHeightEvent> { if (it.hud === hud) it.height = scaledHeight }

            register<GUIEvent.HUD> { onRender(it.widgets) }
        }
}
