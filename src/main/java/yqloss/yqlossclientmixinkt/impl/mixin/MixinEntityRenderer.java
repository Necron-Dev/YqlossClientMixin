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

package yqloss.yqlossclientmixinkt.impl.mixin;

import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yqloss.yqlossclientmixinkt.impl.mixincallback.CallbackEntityRenderer;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {
    @Inject(method = "updateCameraAndRender", at = @At("HEAD"))
    private void yc$updateCameraAndRenderPre(float partialTicks, long nanoTime, CallbackInfo ci) {
        CallbackEntityRenderer.YqlossClient.INSTANCE.updateCameraAndRenderPre(partialTicks);
    }

    @Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(F)V", shift = At.Shift.AFTER))
    private void yc$updateCameraAndRenderRenderHUD(float partialTicks, long nanoTime, CallbackInfo ci) {
        CallbackEntityRenderer.YqlossClient.INSTANCE.updateCameraAndRenderRenderHUD();
    }

    @Inject(method = "updateCameraAndRender", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;currentScreen:Lnet/minecraft/client/gui/GuiScreen;", ordinal = 1))
    private void yc$updateCameraAndRenderProxyScreenAtCondition(float partialTicks, long nanoTime, CallbackInfo ci) {
        CallbackEntityRenderer.YqlossClient.INSTANCE.updateCameraAndRenderProxyScreenAtCondition();
    }

    @Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V"))
    private void yc$ssmotionblur$updateCameraAndRenderRenderMotionBlur(float partialTicks, long nanoTime, CallbackInfo ci) {
        CallbackEntityRenderer.SSMotionBlur.INSTANCE.updateCameraAndRenderRenderMotionBlur();
        CallbackEntityRenderer.Tweaks.INSTANCE.updateCameraAndRenderRenderMotionBlur();
    }
}
