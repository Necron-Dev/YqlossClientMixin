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

package yqloss.yqlossclientmixinkt.impl.option

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.annotations.Number
import cc.polyfrost.oneconfig.config.data.InfoType
import yqloss.yqlossclientmixinkt.YC
import yqloss.yqlossclientmixinkt.api.YCHypixelLocation
import yqloss.yqlossclientmixinkt.api.YCHypixelServerType
import yqloss.yqlossclientmixinkt.impl.YCMixin
import yqloss.yqlossclientmixinkt.impl.option.adapter.Extract
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerAtOwnRisk
import yqloss.yqlossclientmixinkt.module.moduleInfo
import yqloss.yqlossclientmixinkt.module.option.YCModuleOptions
import yqloss.yqlossclientmixinkt.util.MC
import yqloss.yqlossclientmixinkt.util.printChat

class MainConfig : OptionsImpl(moduleInfo<YCModuleOptions>("main", "# Yqloss Client #"), defaultEnabled = true) {
    @Transient
    @Info(type = InfoType.INFO, text = "Ciallo\uFF5E(\u2220\u30FB\u03C9< )\u2312\u2606", size = 2)
    val infoCiallo = false

    @Transient
    @Info(type = InfoType.INFO, text = "已为 OneConfig 添加中文字体支持", size = 2)
    val infoChineseFont = false

    @Transient
    @Extract
    val disclaimer = DisclaimerAtOwnRisk()

    @Transient
    @Header(
        text = "{module.main.config.header.flags}",
        size = 2,
    )
    val headerFlag = false

    @Switch(
        name = "{module.main.config.option.disable_yqloss_client_commands.text}",
        description = "{module.main.config.option.disable_yqloss_client_commands.description}",
        size = 1,
    )
    var disableCommands = false

    @Switch(
        name = "{module.main.config.option.disable_iblockaccess_wrapping.text}",
        description = "{module.main.config.option.disable_iblockaccess_wrapping.description}",
        size = 1,
    )
    var disableBlockAccess = false

    @Number(
        name = "{module.main.config.option.hypixel_partial_server_tick_samples.text}",
        description = "{module.main.config.option.hypixel_partial_server_tick_samples.description}",
        min = 1F,
        max = Float.MAX_VALUE,
        size = 1,
    )
    var hypixelPartialTickSamples = 10

    @Transient
    @Header(
        text = "{module.main.config.header.debug_flags}",
        size = 2,
    )
    val headerDebugFlag = false

    @Switch(
        name = "{module.main.config.option.verbose_hypixel_mod_api.text}",
        description = "{module.main.config.option.verbose_hypixel_mod_api.description}",
        size = 1,
    )
    var verboseHypixelModAPI = false

    @Switch(
        name = "{module.main.config.option.verbose_hypixel_server_tick_duration.text}",
        description = "{module.main.config.option.verbose_hypixel_server_tick_duration.description}",
        size = 1,
    )
    var verboseHypixelServerTickDuration = false

    @Transient
    @Header(
        text = "{module.main.config.header.utilities}",
        size = 2,
    )
    val headerUtilities = false

    @Transient
    @Extract
    val loadAllCharacters =
        @Button(
            name = "{module.main.config.function.load_all_characters.text}",
            text = "{module.main.config.function.load_all_characters.button}",
            description = "{module.main.config.function.load_all_characters.description}",
            size = 1,
        )
        {
            repeat(65536) {
                MC.fontRendererObj.drawString(Char(it).toString(), 0, 0, -1)
            }
        }

    @Transient
    @Header(
        text = "{module.main.config.header.hypixel_mod_api_location}",
        size = 2,
    )
    val headerHypixelModAPILocation = false

    @Transient
    @Extract
    val printHypixelModAPILocation =
        @Button(
            name = "{module.main.config.function.print_hypixel_mod_api_location.text}",
            text = "{module.main.config.function.print_hypixel_mod_api_location.button}",
            description = "{module.main.config.function.print_hypixel_mod_api_location.description}",
            size = 1,
        )
        {
            printChat(YC.api.hypixelLocation.toString())
        }

    @Transient
    @Extract
    val setMineshaft =
        @Button(
            name = "{module.main.config.function.hypixel_mod_api_location_set_mineshaft.text}",
            text = "{module.main.config.function.hypixel_mod_api_location_set_mineshaft.button}",
            description = "{module.main.config.function.hypixel_mod_api_location_set_mineshaft.description}",
            size = 1,
        )
        {
            YCMixin.api.hypixelLocation =
                YCHypixelLocation(
                    "mini0721KLOON",
                    serverType = YCHypixelServerType("SkyBlock"),
                    null,
                    "mineshaft",
                    null,
                )
        }

    @Transient
    @Extract
    val setDungeon =
        @Button(
            name = "{module.main.config.function.hypixel_mod_api_location_set_dungeon.text}",
            text = "{module.main.config.function.hypixel_mod_api_location_set_dungeon.button}",
            description = "{module.main.config.function.hypixel_mod_api_location_set_dungeon.description}",
            size = 1,
        )
        {
            YCMixin.api.hypixelLocation =
                YCHypixelLocation(
                    "mini0721KLOON",
                    serverType = YCHypixelServerType("SkyBlock"),
                    null,
                    "dungeon",
                    "Dungeon",
                )
        }
}
