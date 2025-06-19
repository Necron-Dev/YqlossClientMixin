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
import yqloss.yqlossclientmixinkt.util.TextBuilder.Companion.green
import yqloss.yqlossclientmixinkt.util.TextBuilder.Companion.openUrl
import yqloss.yqlossclientmixinkt.util.TextBuilder.Companion.text
import yqloss.yqlossclientmixinkt.util.TextBuilder.Companion.times
import yqloss.yqlossclientmixinkt.util.TextBuilder.Companion.underlined
import yqloss.yqlossclientmixinkt.util.TextBuilder.Companion.yellow
import yqloss.yqlossclientmixinkt.util.printChat
import yqloss.yqlossclientmixinkt.util.printError

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
        if (latestVersionString === null) return@lazy printError("version api: mod ID is not present in response")

        val updateModNotification = text {
            +green("Update the mod on one of the following websites:")
            +(yellow * underlined) {
                // TODO: waiting for ktlint to update
                +openUrl("https://get.yqloss.net").invoke {
                    +"https://get.yqloss.net"
                }
                // TODO: waiting for ktlint to update
                +openUrl("https://github.com/Necron-Dev/YqlossClientMixin").invoke {
                    +"https://github.com/Necron-Dev/YqlossClientMixin"
                }
            }
        }

        val (latestMajor, latestMinor, latestPatch) = parseVersion(latestVersionString) ?: return@lazy printError {
            +"version api: failed to parse the latest version number"
            +updateModNotification
        }

        val (currentMajor, currentMinor, currentPatch) = parseVersion(YC.modVersion) ?: return@lazy printError {
            +"version api: failed to parse the current version number"
            +"this is probably due to modifications to Yqloss Client (Mixin)"
            +"or the developers' mistakes"
        }

        var comparison = latestMajor - currentMajor
        if (comparison == 0) comparison = latestMinor - currentMinor
        if (comparison == 0) comparison = latestPatch - currentPatch

        when {
            comparison > 0 -> printChat {
                +green {
                    +"There is a new update available for Yqloss Client (Mixin)!"
                    +text {
                        -"Version "
                        -yellow(YC.modVersion)
                        -" -> "
                        -yellow(latestVersionString)
                    }
                }
                +updateModNotification
            }

            comparison < 0 -> printChat {
                +green {
                    +text {
                        -"You are now using the "
                        -yellow(YC.modVersion)
                        -" dev version of Yqloss Client (Mixin)."
                    }
                    +"This version is still under development and any changes could be made before release!"
                }
            }
        }
    }

    fun onTickPre() {
        Repository.options.notifyNewVersion && available && inWorld || return
        notifyNewVersion
    }
}
