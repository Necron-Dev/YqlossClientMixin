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

import net.minecraft.client.gui.ScaledResolution
import net.yqloss.uktil.event.EventRegistry
import net.yqloss.uktil.event.register
import net.yqloss.uktil.math.Vec2D
import yqloss.yqlossclientmixinkt.impl.nanovgui.GUIEvent
import yqloss.yqlossclientmixinkt.impl.nanovgui.Transformation
import yqloss.yqlossclientmixinkt.impl.option.OptionsImpl
import yqloss.yqlossclientmixinkt.module.YCModule
import yqloss.yqlossclientmixinkt.module.option.YCModuleOptions
import yqloss.yqlossclientmixinkt.util.MC

abstract class YCModuleScreenBase<TO, TM : YCModule<in TO>>(
    module: TM,
) : YCModuleGUIBase<TO, TM>(module) where TO : YCModuleOptions, TO : OptionsImpl {
    open val scaleOverride: Double? = null

    override val transformation: Transformation
        get() {
            return ScaledResolution(MC).run {
                Transformation()
                    .translateScreen(Vec2D(0.5, 0.5))
                    .scaleMC(scaleOverride)
                    .translate(-size / 2.0)
            }
        }

    override val registerEvents: EventRegistry.() -> Unit
        get() = {
            super.registerEvents(this)

            register<GUIEvent.Screen> { onRender(it.widgets) }
        }
}
