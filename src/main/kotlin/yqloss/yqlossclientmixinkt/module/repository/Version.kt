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

package yqloss.yqlossclientmixinkt.module.repository

import yqloss.yqlossclientmixinkt.YC
import yqloss.yqlossclientmixinkt.module.inWorld
import yqloss.yqlossclientmixinkt.network.CooldownTypedResource
import yqloss.yqlossclientmixinkt.network.JsonResource
import yqloss.yqlossclientmixinkt.network.TypedResource
import yqloss.yqlossclientmixinkt.network.content
import yqloss.yqlossclientmixinkt.util.printChat
import yqloss.yqlossclientmixinkt.util.printError
import yqloss.yqlossclientmixinkt.util.printURL

const val URL_VERSION = "http://ycm.yqloss.net/version.json"

typealias VersionData = Map<String, String>

class Version : TypedResource<VersionData> by CooldownTypedResource(JsonResource(URL_VERSION), Repository.options.versionCooldown) {
    private val regexVersion = Regex("^(\\d+)\\.(\\d+)\\.(\\d+)$")

    private fun parseVersion(string: String): Triple<Int, Int, Int>? {
        val result = regexVersion.matchEntire(string) ?: return null
        return Triple(
            result.groupValues[1].toInt(),
            result.groupValues[2].toInt(),
            result.groupValues[3].toInt(),
        )
    }

    private val notifyNewVersion by lazy {
        val latestVersionString = content[YC.modID]
        if (latestVersionString === null) {
            printError("version api: mod ID is not present in response")
            return@lazy
        }

        val (latestMajor, latestMinor, latestPatch) = parseVersion(latestVersionString) ?: run {
            printError("\u00A7cversion api: failed to parse the latest version number")
            printChat("\u00A7aUpdate the mod on one of the following websites:")
            printURL("https://get.yqloss.net")
            printURL("https://github.com/Necron-Dev/YqlossClientMixin")
            return@lazy
        }

        val (currentMajor, currentMinor, currentPatch) = parseVersion(YC.modVersion) ?: run {
            printError(
                "version api: failed to parse the current version number\n" +
                    "this is probably due to modifications to Yqloss Client (Mixin)\n" +
                    "or the developers' mistakes",
            )
            return@lazy
        }

        var comparison = latestMajor - currentMajor
        if (comparison == 0) comparison = latestMinor - currentMinor
        if (comparison == 0) comparison = latestPatch - currentPatch

        when {
            comparison > 0 -> {
                printChat("\u00A7aThere is a new update available for Yqloss Client (Mixin)!")
                printChat("\u00A7aVersion \u00A7e${YC.modVersion} \u00A7a-> \u00A7e${content[YC.modID]}")
                printChat("\u00A7aUpdate the mod on one of the following websites:")
                printURL("https://get.yqloss.net")
                printURL("https://github.com/Necron-Dev/YqlossClientMixin")
            }

            comparison < 0 -> {
                printChat("\u00A7aYou are now using the \u00A7e${YC.modVersion} \u00A7adev version of Yqloss Client (Mixin).")
                printChat("\u00A7aThis version is still under development and any changes could be made before release!")
            }
        }
    }

    fun onTickPre() {
        Repository.options.notifyNewVersion && available && inWorld || return
        notifyNewVersion
    }
}
