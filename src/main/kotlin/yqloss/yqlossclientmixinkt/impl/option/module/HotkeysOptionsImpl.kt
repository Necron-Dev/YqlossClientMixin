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

package yqloss.yqlossclientmixinkt.impl.option.module

import cc.polyfrost.oneconfig.config.annotations.Header
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import yqloss.yqlossclientmixinkt.impl.option.OptionsImpl
import yqloss.yqlossclientmixinkt.impl.option.adapter.Extract
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerAtOwnRisk
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerLegit
import yqloss.yqlossclientmixinkt.module.hotkeys.HotkeysOptions
import yqloss.yqlossclientmixinkt.module.hotkeys.INFO_HOTKEYS
import yqloss.yqlossclientmixinkt.module.option.YCModuleOptions
import yqloss.yqlossclientmixinkt.util.MC

class HotkeysOptionsImpl :
    OptionsImpl(INFO_HOTKEYS),
    HotkeysOptions {
    @Transient
    @Extract
    val disclaimer = DisclaimerAtOwnRisk()

    @Transient
    @Extract
    val legit = DisclaimerLegit()

    @Transient
    @Header(
        text = "{module.hotkeys.config.header.module}",
        size = 2,
    )
    val headerModule = false

    @KeyBind(
        name = "{module.hotkeys.config.option.drop_single_item.text}",
        description = "{module.hotkeys.config.option.drop_single_item.description}",
    )
    var keyBindDropSingleItem = OneKeyBind()

    @KeyBind(
        name = "{module.hotkeys.config.option.drop_item_stack.text}",
        description = "{module.hotkeys.config.option.drop_item_stack.description}",
    )
    var keyBindDropItemStack = OneKeyBind()

    override fun onInitializationPost() {
        registerKeyBind(keyBindDropSingleItem) {
            if (!(this as YCModuleOptions).enabled) return@registerKeyBind
            MC.theWorld ?: return@registerKeyBind
            if (!MC.thePlayer.isSpectator) {
                MC.thePlayer.dropOneItem(false)
            }
        }

        registerKeyBind(keyBindDropItemStack) {
            if (!(this as YCModuleOptions).enabled) return@registerKeyBind
            MC.theWorld ?: return@registerKeyBind
            if (!MC.thePlayer.isSpectator) {
                MC.thePlayer.dropOneItem(true)
            }
        }
    }
}
