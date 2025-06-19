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
import cc.polyfrost.oneconfig.config.annotations.Switch
import yqloss.yqlossclientmixinkt.impl.option.OptionsImpl
import yqloss.yqlossclientmixinkt.impl.option.adapter.Extract
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerAtOwnRisk
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerQOL
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerSafeBlatantSkyBlock
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerUnknownBlatant
import yqloss.yqlossclientmixinkt.module.tweaks.INFO_TWEAKS
import yqloss.yqlossclientmixinkt.module.tweaks.TweaksOptions

class TweaksOptionsImpl :
    OptionsImpl(INFO_TWEAKS),
    TweaksOptions {
    @Transient
    @Extract
    val disclaimer = DisclaimerAtOwnRisk()

    @Transient
    @Header(
        text = "Tweaks",
        size = 2,
    )
    val headerModule = false

    @Transient
    @Extract
    val qol = DisclaimerQOL()

    @Switch(
        name = "Enable Instant Aim",
        description = "Set prevRotationYawHead and rotationYawHead of EntityPlayerSP to prevRotationYaw and rotationYaw.",
        size = 1,
    )
    var enableInstantAimOption = false

    @Switch(
        name = "Enable Catacombs Boss Bar Fix",
        size = 1,
    )
    var enableCatacombsBossBarFixOption = false

    @Transient
    @Extract
    val safeBlatantSkyBlock = DisclaimerSafeBlatantSkyBlock()

    @Switch(
        name = "Disable NBT Update Reset Digging on SkyBlock Mining Islands",
        description = "This applies to all pickaxes, prismarine shards, skulls on these islands: Gold Mine, Deep Caverns, " +
            "Dwarven Mines, Crystal Hollows, Mineshaft, The End, Crimson Isle.",
        size = 1,
    )
    var disableSkyBlockToolsNBTUpdateResetDiggingOption = false

    @Transient
    @Extract
    val unknownBlatant = DisclaimerUnknownBlatant()

    @Switch(
        name = "Disable Pearl Click-On-Block Packet",
        description = "Cancel the first C08PacketPlayerBlockPlacement packet when throwing an ender pearl while aiming at a block. " +
            "This fixes not being able to throw pearls while aiming at a block on public islands in Hypixel SkyBlock.",
        size = 1,
    )
    var disablePearlClickBlockOption = false

    override val enableInstantAim by ::enableInstantAimOption
    override val disablePearlClickBlock by ::disablePearlClickBlockOption
    override val disableSkyBlockToolsNBTUpdateResetDigging by ::disableSkyBlockToolsNBTUpdateResetDiggingOption
    override val enableCatacombsBossBarFix by ::enableCatacombsBossBarFixOption

    override fun onInitializationPost() {
        requirePlus(
            "safeBlatantSkyBlock",
            "disableSkyBlockToolsNBTUpdateResetDiggingOption",
        )

        requireEx(
            "unknownBlatant",
            "disablePearlClickBlockOption",
        )
    }
}
