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

import cc.polyfrost.oneconfig.gui.elements.config.ConfigDropdown;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yqloss.yqlossclientmixinkt.YqlossClientKt;
import yqloss.yqlossclientmixinkt.api.YCAPI;
import yqloss.yqlossclientmixinkt.impl.option.YqlossClientConfigKt;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

@Mixin(ConfigDropdown.class)
public abstract class MixinConfigDropdown {
    @Unique
    private static final Field yc$originalOptionsField;

    @Unique
    private static final Field yc$optionsField;

    static {
        try {
            yc$originalOptionsField = ConfigDropdown.class.getDeclaredField("originalOptions");
            yc$originalOptionsField.setAccessible(true);

            yc$optionsField = ConfigDropdown.class.getDeclaredField("options");
            yc$optionsField.setAccessible(true);
        } catch (NoSuchFieldException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void yc$modify(Field field, Object parent, String name, String description, String category, String subcategory, int size, String[] options, CallbackInfo ci) throws Exception {
        if (!YqlossClientConfigKt.getSettingUpYqlossClientConfig()) return;

        YCAPI api = YqlossClientKt.getYC().getApi();

        String[] copiedOptions = Arrays.copyOf(options, options.length);

        for (int i = 0; i < copiedOptions.length; ++i) {
            copiedOptions[i] = api.translate(copiedOptions[i]);
        }

        yc$originalOptionsField.set(this, copiedOptions);
        yc$optionsField.set(this, new ArrayList<>(Arrays.asList(copiedOptions)));
    }
}
