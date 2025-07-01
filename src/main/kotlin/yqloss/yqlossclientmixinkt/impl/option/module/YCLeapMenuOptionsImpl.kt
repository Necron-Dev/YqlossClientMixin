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
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import yqloss.yqlossclientmixinkt.impl.module.ycleapmenu.YCLeapMenuScreen
import yqloss.yqlossclientmixinkt.impl.option.OptionsImpl
import yqloss.yqlossclientmixinkt.impl.option.adapter.Extract
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerAtOwnRisk
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerLegit
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerRequireHypixelModAPI
import yqloss.yqlossclientmixinkt.impl.option.impl.NotificationOption
import yqloss.yqlossclientmixinkt.impl.option.impl.ScreenScaleOption
import yqloss.yqlossclientmixinkt.module.ycleapmenu.INFO_YC_LEAP_MENU
import yqloss.yqlossclientmixinkt.module.ycleapmenu.YCLeapMenuOptions

class YCLeapMenuOptionsImpl :
    OptionsImpl(INFO_YC_LEAP_MENU),
    YCLeapMenuOptions {
    @Transient
    @Extract
    val disclaimer = DisclaimerAtOwnRisk()

    @Transient
    @Extract
    val legit = DisclaimerLegit()

    @Transient
    @Extract
    val requireHypixelModAPI = DisclaimerRequireHypixelModAPI()

    @Transient
    @Header(
        text = "{module.yc_leap_menu.config.header.module}",
        size = 2,
    )
    val headerModule = false

    @Extract
    var scaleOverride = ScreenScaleOption()

    @Switch(
        name = "{module.yc_leap_menu.config.option.smooth_gui.text}",
        description = "{module.yc_leap_menu.config.option.smooth_gui.description}",
        size = 1,
    )
    var smoothGUI = true

    @Transient
    @Header(
        text = "{module.yc_leap_menu.config.header.hotkeys}",
        size = 2,
    )
    val headerHotkeys = false

    @KeyBind(
        name = "{module.yc_leap_menu.config.option.keybind_index_0.text}",
        description = "{module.yc_leap_menu.config.option.keybind_index_0.description}",
    )
    var keyBindIndex0 = OneKeyBind()

    @KeyBind(
        name = "{module.yc_leap_menu.config.option.keybind_index_1.text}",
        description = "{module.yc_leap_menu.config.option.keybind_index_1.description}",
    )
    var keyBindIndex1 = OneKeyBind()

    @KeyBind(
        name = "{module.yc_leap_menu.config.option.keybind_index_2.text}",
        description = "{module.yc_leap_menu.config.option.keybind_index_2.description}",
    )
    var keyBindIndex2 = OneKeyBind()

    @KeyBind(
        name = "{module.yc_leap_menu.config.option.keybind_index_3.text}",
        description = "{module.yc_leap_menu.config.option.keybind_index_3.description}",
    )
    var keyBindIndex3 = OneKeyBind()

    @KeyBind(
        name = "{module.yc_leap_menu.config.option.keybind_index_4.text}",
        description = "{module.yc_leap_menu.config.option.keybind_index_4.description}",
    )
    var keyBindIndex4 = OneKeyBind()

    val keyBinds: List<OneKeyBind>
        get() {
            return listOf(
                keyBindIndex0,
                keyBindIndex1,
                keyBindIndex2,
                keyBindIndex3,
                keyBindIndex4,
            )
        }

    override fun onInitializationPost() {
        keyBinds.forEachIndexed { i, keyBind ->
            keyBind.setRunnable {
                if (YCLeapMenuScreen.ensureShow) {
                    YCLeapMenuScreen.clickButton(i)
                }
            }
        }
    }

    @Transient
    @Header(
        text = "{module.yc_leap_menu.config.header.notification_on_clicking_leap_button}",
        size = 2,
    )
    val headerNotification = false

    @Extract
    var onClickLeapOption = NotificationOption()

    @Transient
    @Header(
        text = "{module.yc_leap_menu.config.header.debug}",
        size = 2,
    )
    val headerDebug = false

    @Switch(
        name = "{module.yc_leap_menu.config.option.force_enabled.text}",
        description = "{module.yc_leap_menu.config.option.force_enabled.description}",
        size = 1,
    )
    var forceEnabledOption = false

    override val onClickLeap by ::onClickLeapOption
    override val forceEnabled by ::forceEnabledOption
}
