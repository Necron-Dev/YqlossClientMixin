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

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.HUD;
import cc.polyfrost.oneconfig.config.elements.OptionPage;
import cc.polyfrost.oneconfig.hud.HUDUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yqloss.yqlossclientmixinkt.YqlossClientKt;
import yqloss.yqlossclientmixinkt.api.YCAPI;
import yqloss.yqlossclientmixinkt.impl.option.OptionsImplKt;
import yqloss.yqlossclientmixinkt.impl.option.YqlossClientConfigKt;

import java.lang.reflect.Field;
import java.util.List;

@Mixin(HUDUtils.class)
public abstract class MixinHUDUtils {
    @Inject(method = "addHudOptions", at = @At("HEAD"), remap = false)
    private static void yc$addHudOptionsPre(OptionPage page, Field field, Object instance, Config config, CallbackInfo ci) {
        YqlossClientConfigKt.setSettingUpHUD(true);
    }

    @Inject(method = "addHudOptions", at = @At("RETURN"), remap = false)
    private static void yc$addHudOptionsPost(OptionPage page, Field field, Object instance, Config config, CallbackInfo ci) {
        YqlossClientConfigKt.setSettingUpHUD(false);
    }

    @Redirect(method = "addHudOptions", at = @At(value = "INVOKE", target = "Lcc/polyfrost/oneconfig/config/annotations/HUD;category()Ljava/lang/String;", ordinal = 1), remap = false)
    private static String yc$modifyCategory(HUD instance) {
        String category = instance.category();

        if (!YqlossClientConfigKt.getSettingUpYqlossClientConfig()) return category;

        YCAPI api = YqlossClientKt.getYC().getApi();

        List<String> categoryOverride = OptionsImplKt.getCategoryOverride();

        if (!categoryOverride.isEmpty()) {
            category = categoryOverride.get(categoryOverride.size() - 1);
        }

        if ("General".equals(category)) {
            category = "{config.category.default}";
        }

        return api.translate(category);
    }

    @Redirect(method = "addHudOptions", at = @At(value = "INVOKE", target = "Lcc/polyfrost/oneconfig/config/annotations/HUD;subcategory()Ljava/lang/String;", ordinal = 1), remap = false)
    private static String yc$modifySubCategory(HUD instance) {
        String subCategory = instance.subcategory();

        if (!YqlossClientConfigKt.getSettingUpYqlossClientConfig()) return subCategory;

        YCAPI api = YqlossClientKt.getYC().getApi();

        List<String> subCategoryOverride = OptionsImplKt.getSubCategoryOverride();

        if (!subCategoryOverride.isEmpty()) {
            subCategory = subCategoryOverride.get(subCategoryOverride.size() - 1);
        }

        return api.translate(subCategory);
    }
}
