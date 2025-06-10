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

import cc.polyfrost.oneconfig.config.annotations.Button
import cc.polyfrost.oneconfig.config.annotations.Header
import cc.polyfrost.oneconfig.config.annotations.Number
import cc.polyfrost.oneconfig.config.annotations.Switch
import net.yqloss.uktil.extension.double
import yqloss.yqlossclientmixinkt.impl.option.OptionsImpl
import yqloss.yqlossclientmixinkt.impl.option.adapter.Extract
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerAtOwnRisk
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerLegit
import yqloss.yqlossclientmixinkt.module.repository.INFO_REPOSITORY
import yqloss.yqlossclientmixinkt.module.repository.Repository
import yqloss.yqlossclientmixinkt.module.repository.RepositoryOptions

class RepositoryOptionsImpl :
    OptionsImpl(INFO_REPOSITORY, true),
    RepositoryOptions {
    @Transient
    @Extract
    val disclaimer = DisclaimerAtOwnRisk()

    @Transient
    @Extract
    val legit = DisclaimerLegit()

    @Transient
    @Header(
        text = "Repository",
        size = 2,
    )
    val headerModule = false

    @Transient
    @Header(
        text = "Version API",
        size = 2,
    )
    val headerVersion = false

    @Switch(
        name = "Enable Requesting",
        size = 1,
    )
    var versionEnabledOption = true

    @Transient
    @Extract
    val reloadVersion =
        @Button(
            name = "Reload API",
            text = "Reload",
            size = 1,
        )
        {
            Repository.reloadVersion()
        }

    @Number(
        name = "Request Cooldown",
        min = 0F,
        max = Float.MAX_VALUE,
        size = 1,
    )
    var versionCooldownOption = 60F

    @Switch(
        name = "Notify New Version",
        size = 1,
    )
    var notifyNewVersionOption = true

    @Transient
    @Header(
        text = "Cape API",
        size = 2,
    )
    val headerCape = false

    @Switch(
        name = "Enable Requesting",
        size = 1,
    )
    var capeEnabledOption = true

    @Transient
    @Extract
    val reloadCape =
        @Button(
            name = "Reload API",
            text = "Reload",
            size = 1,
        )
        {
            Repository.reloadCapes()
        }

    @Number(
        name = "Request Cooldown",
        min = 0F,
        max = Float.MAX_VALUE,
        size = 1,
    )
    var capeCooldownOption = 60F

    @Number(
        name = "Metadata Request Cooldown",
        min = 0F,
        max = Float.MAX_VALUE,
        size = 1,
    )
    var capeMetadataCooldownOption = 60F

    @Number(
        name = "Texture Request Cooldown",
        min = 0F,
        max = Float.MAX_VALUE,
        size = 1,
    )
    var capeTextureCooldownOption = 60F

    @Switch(
        name = "Show Mod Capes",
        size = 1,
    )
    var showModCapesOption = true

    override val versionEnabled by ::versionEnabledOption
    override val versionCooldown get() = versionCooldownOption.double
    override val notifyNewVersion by ::notifyNewVersionOption
    override val capeEnabled by ::capeEnabledOption
    override val capeCooldown get() = capeCooldownOption.double
    override val capeMetadataCooldown get() = capeMetadataCooldownOption.double
    override val capeTextureCooldown get() = capeTextureCooldownOption.double
    override val showModCapes by ::showModCapesOption
}
