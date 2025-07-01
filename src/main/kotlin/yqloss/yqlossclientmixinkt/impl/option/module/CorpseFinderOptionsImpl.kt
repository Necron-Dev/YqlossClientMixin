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

import cc.polyfrost.oneconfig.config.annotations.Color
import cc.polyfrost.oneconfig.config.annotations.Header
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneColor
import net.yqloss.uktil.extension.type.ifTake
import yqloss.yqlossclientmixinkt.impl.option.OptionsImpl
import yqloss.yqlossclientmixinkt.impl.option.adapter.Extract
import yqloss.yqlossclientmixinkt.impl.option.adapter.asYCColor
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerAtOwnRisk
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerLegit
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerRequireHypixelModAPI
import yqloss.yqlossclientmixinkt.impl.option.impl.NotificationOption
import yqloss.yqlossclientmixinkt.module.corpsefinder.CorpseFinderOptions
import yqloss.yqlossclientmixinkt.module.corpsefinder.CorpseOption
import yqloss.yqlossclientmixinkt.module.corpsefinder.INFO_CORPSE_FINDER

class CorpseOptionImpl : CorpseOption {
    @Switch(
        name = "{module.corpse_finder.config.corpse.option.show_box.text}",
        description = "{module.corpse_finder.config.corpse.option.show_box.description}",
        size = 1,
    )
    var show = false

    @Color(
        name = "{module.corpse_finder.config.corpse.option.box_color.text}",
        description = "{module.corpse_finder.config.corpse.option.box_color.description}",
        size = 1,
    )
    var colorOption = OneColor("FFFFFFFF")

    @Extract
    var notificationOption = NotificationOption()

    override val color get() = show.ifTake { colorOption.asYCColor }
    override val notification by ::notificationOption
}

class CorpseFinderOptionsImpl :
    OptionsImpl(INFO_CORPSE_FINDER),
    CorpseFinderOptions {
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
        text = "{module.corpse_finder.config.header.module}",
        size = 2,
    )
    val headerModule = false

    @Switch(
        name = "{module.corpse_finder.config.option.show_exit_box.text}",
        description = "{module.corpse_finder.config.option.show_exit_box.description}",
        size = 1,
    )
    var showExitOption = false

    @Color(
        name = "{module.corpse_finder.config.option.exit_box_color.text}",
        description = "{module.corpse_finder.config.option.exit_box_color.description}",
        size = 1,
    )
    var exitColorOption = OneColor("00FF00FF")

    @Transient
    @Header(
        text = "{module.corpse_finder.config.header.lapis_corpse_options}",
        size = 2,
    )
    val headerLapis = false

    @Extract
    var lapisOption =
        CorpseOptionImpl().apply {
            colorOption = OneColor("5555FFFF")
        }

    @Transient
    @Header(
        text = "{module.corpse_finder.config.header.umber_corpse_options}",
        size = 2,
    )
    val headerUmber = false

    @Extract
    var umberOption =
        CorpseOptionImpl().apply {
            colorOption = OneColor("FFAA00FF")
        }

    @Transient
    @Header(
        text = "{module.corpse_finder.config.header.tungsten_corpse_options}",
        size = 2,
    )
    val headerTungsten = false

    @Extract
    var tungstenOption =
        CorpseOptionImpl().apply {
            colorOption = OneColor("AAAAAAFF")
        }

    @Transient
    @Header(
        text = "{module.corpse_finder.config.header.vanguard_corpse_options}",
        size = 2,
    )
    val headerVanguard = false

    @Extract
    var vanguardOption =
        CorpseOptionImpl().apply {
            colorOption = OneColor("FFFF55FF")
        }

    @Transient
    @Header(
        text = "{module.corpse_finder.config.header.mobs}",
        size = 2,
    )
    val headerMobs = false

    @Switch(
        name = "{module.corpse_finder.config.option.show_bowman_box.text}",
        description = "{module.corpse_finder.config.option.show_bowman_box.description}",
        size = 1,
    )
    var showBowman = false

    @Color(
        name = "{module.corpse_finder.config.option.bowman_box_color.text}",
        description = "{module.corpse_finder.config.option.bowman_box_color.description}",
        size = 1,
    )
    var bowmanColorOption = OneColor("FF0000FF")

    @Switch(
        name = "{module.corpse_finder.config.option.show_caver_box.text}",
        description = "{module.corpse_finder.config.option.show_caver_box.description}",
        size = 1,
    )
    var showCaver = false

    @Color(
        name = "{module.corpse_finder.config.option.caver_box_color.text}",
        description = "{module.corpse_finder.config.option.caver_box_color.description}",
        size = 1,
    )
    var caverColorOption = OneColor("FF0000FF")

    @Switch(
        name = "{module.corpse_finder.config.option.show_mage_box.text}",
        description = "{module.corpse_finder.config.option.show_mage_box.description}",
        size = 1,
    )
    var showMage = false

    @Color(
        name = "{module.corpse_finder.config.option.mage_box_color.text}",
        description = "{module.corpse_finder.config.option.mage_box_color.description}",
        size = 1,
    )
    var mageColorOption = OneColor("FF0000FF")

    @Switch(
        name = "{module.corpse_finder.config.option.show_mutt_box.text}",
        description = "{module.corpse_finder.config.option.show_mutt_box.description}",
        size = 1,
    )
    var showMutt = false

    @Color(
        name = "{module.corpse_finder.config.option.mutt_box_color.text}",
        description = "{module.corpse_finder.config.option.mutt_box_color.description}",
        size = 1,
    )
    var muttColorOption = OneColor("FF0000FF")

    @Transient
    @Header(
        text = "{module.corpse_finder.config.header.debug}",
        size = 2,
    )
    val headerDebug = false

    @Switch(
        name = "{module.corpse_finder.config.option.force_enabled.text}",
        description = "{module.corpse_finder.config.option.force_enabled.description}",
        size = 1,
    )
    var forceEnabledOption = false

    override val exitColor get() = showExitOption.ifTake { exitColorOption.asYCColor }
    override val lapis by ::lapisOption
    override val umber by ::umberOption
    override val tungsten by ::tungstenOption
    override val vanguard by ::vanguardOption
    override val bowmanColor get() = showBowman.ifTake { bowmanColorOption.asYCColor }
    override val caverColor get() = showCaver.ifTake { caverColorOption.asYCColor }
    override val mageColor get() = showMage.ifTake { mageColorOption.asYCColor }
    override val muttColor get() = showMutt.ifTake { muttColorOption.asYCColor }
    override val forceEnabled by ::forceEnabledOption
}
