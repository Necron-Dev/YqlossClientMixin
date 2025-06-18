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

package yqloss.yqlossclientmixinkt.impl.mixincallback

import net.minecraft.client.gui.ScaledResolution
import yqloss.yqlossclientmixinkt.YC
import yqloss.yqlossclientmixinkt.event.minecraft.YCRenderEvent
import yqloss.yqlossclientmixinkt.impl.nanovgui.GUIEvent
import yqloss.yqlossclientmixinkt.module.ssmotionblur.SSMotionBlurEvent
import yqloss.yqlossclientmixinkt.module.tweaks.TweaksEvent
import yqloss.yqlossclientmixinkt.util.MC
import yqloss.yqlossclientmixinkt.util.partialTicks

object CallbackEntityRenderer {
    object YqlossClient {
        fun updateCameraAndRenderPre(partialTicksIn: Double) {
            partialTicks = partialTicksIn
            YC.eventDispatcher(YCRenderEvent.Render.Pre)
        }

        fun updateCameraAndRenderRenderHUD() {
            GUIEvent.HUD
                .Post()
                .also(YC.eventDispatcher)
                .render()
        }

        fun updateCameraAndRenderProxyScreenAtCondition() {
            if (MC.currentScreen !== null) return
            GUIEvent.Screen
                .Post()
                .also(YC.eventDispatcher)
                .render()
        }
    }

    object SSMotionBlur {
        fun updateCameraAndRenderRenderMotionBlur() {
            YC.eventDispatcher(SSMotionBlurEvent.Render(MC.displayWidth, MC.displayHeight, ScaledResolution(MC)))
        }
    }

    object Tweaks {
        fun updateCameraAndRenderRenderMotionBlur() {
            YC.eventDispatcher(TweaksEvent.RenderHUDPre)
        }
    }
}
