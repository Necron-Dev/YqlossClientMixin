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

import cc.polyfrost.oneconfig.gui.elements.BasicButton;
import cc.polyfrost.oneconfig.renderer.asset.SVG;
import cc.polyfrost.oneconfig.utils.color.ColorPalette;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yqloss.yqlossclientmixinkt.YqlossClientKt;
import yqloss.yqlossclientmixinkt.api.YCAPI;
import yqloss.yqlossclientmixinkt.impl.option.YqlossClientConfigKt;

import java.lang.reflect.Field;

@Mixin(BasicButton.class)
public abstract class MixinBasicButton {
    @Unique
    private static final Field yc$textField;

    static {
        try {
            yc$textField = BasicButton.class.getDeclaredField("text");
            yc$textField.setAccessible(true);
        } catch (NoSuchFieldException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Inject(method = "<init>(IIIIILjava/lang/String;Lcc/polyfrost/oneconfig/renderer/asset/SVG;Lcc/polyfrost/oneconfig/renderer/asset/SVG;ILcc/polyfrost/oneconfig/utils/color/ColorPalette;)V", at = @At("RETURN"), remap = false)
    private void yc$modify(int width, int size, int iconSize, int xSpacing, int xPadding, String text, SVG icon1, SVG icon2, int align, ColorPalette colorPalette, CallbackInfo ci) throws Exception {
        if (text == null || !YqlossClientConfigKt.getSettingUpYqlossClientConfig()) return;

        YCAPI api = YqlossClientKt.getYC().getApi();

        yc$textField.set(this, api.translate(text));
    }
}
