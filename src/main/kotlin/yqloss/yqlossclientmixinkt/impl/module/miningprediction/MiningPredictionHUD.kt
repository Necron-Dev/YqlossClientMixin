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

package yqloss.yqlossclientmixinkt.impl.module.miningprediction

import net.yqloss.uktil.event.EventRegistry
import net.yqloss.uktil.event.register
import net.yqloss.uktil.extension.double
import net.yqloss.uktil.math.Fraction
import net.yqloss.uktil.math.Vec2D
import net.yqloss.uktil.scope.longRet
import yqloss.yqlossclientmixinkt.YC
import yqloss.yqlossclientmixinkt.api.format
import yqloss.yqlossclientmixinkt.event.hypixel.hypixelPartialServerTicks
import yqloss.yqlossclientmixinkt.impl.module.YCModuleHUDBase
import yqloss.yqlossclientmixinkt.impl.nanovgui.Transformation
import yqloss.yqlossclientmixinkt.impl.nanovgui.Widget
import yqloss.yqlossclientmixinkt.impl.nanovgui.widget.TextWidget
import yqloss.yqlossclientmixinkt.impl.nanovgui.widget.progressBarWidget
import yqloss.yqlossclientmixinkt.impl.oneconfiginternal.fontSemiBold
import yqloss.yqlossclientmixinkt.impl.option.module.MiningPredictionOptionsImpl
import yqloss.yqlossclientmixinkt.module.miningprediction.MiningPrediction
import yqloss.yqlossclientmixinkt.module.miningprediction.MiningPredictionEvent
import yqloss.yqlossclientmixinkt.util.partialTicks
import kotlin.math.min

object MiningPredictionHUD :
    YCModuleHUDBase<MiningPredictionOptionsImpl, MiningPrediction>(MiningPrediction, { options.hud }) {
    override val width get() = options.width.double

    override val height get() = (options.textSize + options.textProgressGap + options.progressHeight).double

    override val fadeOut = 3000000000L

    override val example get() = super.example || options.forceExample

    override val ensureShow get() = isHUDEnabled && (example || module.isAvailable && module.breakingBlock !== null)

    override fun draw(
        widgets: MutableList<Widget<*>>,
        box: Vec2D,
        tr: Transformation,
    ) {
        val progress =
            if (example) {
                0.5
            } else {
                val partial = if (options.useClientTickOption) partialTicks else hypixelPartialServerTicks
                min(1.0, module.breakingProgress.double + partial * module.getProgressPerTick().double)
            }

        val textLeft =
            if (example) {
                "Bedrock"
            } else {
                YC.api.format(options.textLeft, module::setPlaceholders)
            }

        val textRight =
            if (example) {
                "5 / 10"
            } else {
                YC.api.format(options.textRight, module::setPlaceholders)
            }

        val foreground =
            if (example) {
                options.progressForeground.rgb
            } else if (module.breakingProgress >= Fraction.ONE) {
                options.progressForegroundOnBreak.rgb
            } else {
                options.progressForeground.rgb
            }

        options.background.addTo(widgets, tr, box)

        widgets.add(
            progressBarWidget(
                progress,
                tr pos Vec2D(0.0, box.y - options.progressHeight.double),
                tr pos box,
                foreground,
                options.progressBackground.rgb,
            ),
        )

        widgets.add(
            TextWidget(
                textLeft,
                tr pos Vec2D(0.0, 0.0),
                options.textColorLeft.rgb,
                tr size options.textSize.double,
                fontSemiBold,
                Vec2D(0.0, 0.0),
            ),
        )

        widgets.add(
            TextWidget(
                textRight,
                tr pos Vec2D(box.x, 0.0),
                options.textColorRight.rgb,
                tr size options.textSize.double,
                fontSemiBold,
                Vec2D(1.0, 0.0),
            ),
        )
    }

    override val registerEvents: EventRegistry.() -> Unit
        get() = {
            super.registerEvents(this)

            register<MiningPredictionEvent.BreakBlock> {
                isHUDEnabled || longRet

                val box = size
                val tr = transformation
                animation.update(true, box, tr, fadeOut)
                redraw(box, tr)
            }
        }
}
