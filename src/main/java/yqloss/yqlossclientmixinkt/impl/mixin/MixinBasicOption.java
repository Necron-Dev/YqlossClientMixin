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

import cc.polyfrost.oneconfig.config.elements.BasicOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yqloss.yqlossclientmixinkt.impl.option.OptionsImplKt;

import java.lang.reflect.Field;
import java.util.List;

@Mixin(BasicOption.class)
public abstract class MixinBasicOption {
    @Unique
    private static final Field yc$categoryField;

    @Unique
    private static final Field yc$subcategoryField;

    static {
        try {
            yc$categoryField = BasicOption.class.getDeclaredField("category");
            yc$categoryField.setAccessible(true);

            yc$subcategoryField = BasicOption.class.getDeclaredField("subcategory");
            yc$subcategoryField.setAccessible(true);
        } catch (NoSuchFieldException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void setOverrideCategory(Field field, Object parent, String name, String description, String category, String subcategory, int size, CallbackInfo ci) throws Exception {
        List<String> categoryOverride = OptionsImplKt.getCategoryOverride();
        List<String> subCategoryOverride = OptionsImplKt.getSubCategoryOverride();

        if (!categoryOverride.isEmpty()) {
            yc$categoryField.set(this, categoryOverride.get(categoryOverride.size() - 1));
        }

        if (!subCategoryOverride.isEmpty()) {
            yc$subcategoryField.set(this, subCategoryOverride.get(subCategoryOverride.size() - 1));
        }
    }
}
