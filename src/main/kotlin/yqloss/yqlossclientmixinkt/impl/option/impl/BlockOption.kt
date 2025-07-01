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
import cc.polyfrost.oneconfig.config.annotations.Text
import yqloss.yqlossclientmixinkt.module.option.YCBlockOption

class BlockOption : YCBlockOption {
    @Text(
        name = "{config.block.option.block_id.text}",
        description = "{config.block.option.block_id.description}",
        size = 1,
    )
    var idOption = "minecraft:air"

    @Number(
        name = "{config.block.option.block_metadata.text}",
        description = "{config.block.option.block_metadata.description}",
        min = 0.0F,
        max = 65535.0F,
        step = 1,
        size = 1,
    )
    var metaOption = 0

    override val id by ::idOption
    override val meta by ::metaOption
}
