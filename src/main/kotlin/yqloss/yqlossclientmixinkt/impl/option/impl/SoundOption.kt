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

package yqloss.yqlossclientmixinkt.impl.option.impl

import cc.polyfrost.oneconfig.config.annotations.Number
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.annotations.Text
import net.yqloss.uktil.extension.double
import yqloss.yqlossclientmixinkt.module.option.YCSoundOption

class SoundOption : YCSoundOption {
    @Switch(
        name = "{config.notification.sound.option.enabled.text}",
        description = "{config.notification.sound.option.enabled.description}",
        size = 1,
    )
    var enabledOption = false

    @Text(
        name = "{config.notification.sound.option.name.text}",
        description = "{config.notification.sound.option.name.description}",
        size = 1,
    )
    var nameOption = ""

    @Number(
        name = "{config.notification.sound.option.volume.text}",
        description = "{config.notification.sound.option.volume.description}",
        min = 0.0F,
        max = 1.0F,
        size = 1,
    )
    var volumeOption = 1.0F

    @Number(
        name = "{config.notification.sound.option.pitch.text}",
        description = "{config.notification.sound.option.pitch.description}",
        min = 0.0F,
        max = 2.0F,
        size = 1,
    )
    var pitchOption = 1.0F

    override val enabled by ::enabledOption
    override val name by ::nameOption
    override val volume get() = volumeOption.double
    override val pitch get() = pitchOption.double
}
