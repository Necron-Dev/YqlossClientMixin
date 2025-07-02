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

import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.internal.config.core.ConfigCore

fun OneKeyBind.handle() {
    if (isActive) run()
}

fun removeMod(mod: Mod) {
    ConfigCore.mods.remove(mod)
    ConfigCore.subMods[mod]?.forEach(::removeMod)
}

fun removeSubMod(mod: Mod) {
    ConfigCore.subMods.values.forEach {
        it.remove(mod)
    }
    removeMod(mod)
}

private fun hudOption(path: String) = "{config.hud.$path}"

private val HUD_OPTION_MAP = mapOf(
    "enabled" to "option.enabled",
    "locked" to "option.locked",
    "ignoreCaching" to "option.ignore_caching",
    "scale" to "option.scale",
    "positionAlignment" to "option.position_alignment",
    "background" to "option.background",
    "rounded" to "option.rounded",
    "border" to "option.border",
    "bgColor" to "option.background_color",
    "borderColor" to "option.border_color",
    "cornerRadius" to "option.corner_radius",
    "borderSize" to "option.border_size",
    "paddingX" to "option.padding_x",
    "paddingY" to "option.padding_y",
    "showInChat" to "option.show_in_chat",
    "showInDebug" to "option.show_in_debug",
    "showInGuis" to "option.show_in_guis",
    "resetPosition" to "function.reset_position",
)

fun mapHUDOption(fieldName: String) = HUD_OPTION_MAP[fieldName]?.let {
    hudOption("$it.text") to hudOption("$it.description")
} ?: (null to null)

fun mapHUDDropdownOptions(fieldName: String, array: Array<String>) {
    if (fieldName == "positionAlignment") {
        repeat(array.size) {
            array[it] = hudOption("option.position_alignment.options.$it")
        }
    }
}

fun mapHUDButtonText(text: String): String {
    if (text == "Reset") return hudOption("function.reset_position.button")
    return text
}
