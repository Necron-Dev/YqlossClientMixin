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
import yqloss.yqlossclientmixinkt.api.formatTranslated
import yqloss.yqlossclientmixinkt.module.inWorld
import yqloss.yqlossclientmixinkt.network.CooldownTypedResource
import yqloss.yqlossclientmixinkt.network.JsonResource
import yqloss.yqlossclientmixinkt.network.TypedResource
import yqloss.yqlossclientmixinkt.network.content
import yqloss.yqlossclientmixinkt.util.TextBuilderContext.Companion.invoke
import yqloss.yqlossclientmixinkt.util.TextBuilderContext.Companion.openUrl
import yqloss.yqlossclientmixinkt.util.TextBuilderContext.Companion.text
import yqloss.yqlossclientmixinkt.util.TextBuilderContext.Companion.times
import yqloss.yqlossclientmixinkt.util.TextBuilderContext.Companion.underlined
import yqloss.yqlossclientmixinkt.util.TextBuilderContext.Companion.yellow
import yqloss.yqlossclientmixinkt.util.printChat
import yqloss.yqlossclientmixinkt.util.printChatTranslated
import yqloss.yqlossclientmixinkt.util.printError
import yqloss.yqlossclientmixinkt.util.printErrorTranslated

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
            return@lazy printErrorTranslated("{module.repository.message.version.error_mod_id_not_present}")
        }

        val updateModNotification = text {
            +formatTranslated("{module.repository.message.version.update_links}")
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
            +formatTranslated("{module.repository.message.version.error_parsing_latest}")
            +updateModNotification
        }

        val (currentMajor, currentMinor, currentPatch) = parseVersion(YC.modVersion) ?: return@lazy printError {
            +formatTranslated("{module.repository.message.version.error_parsing_current}")
        }

        var comparison = latestMajor - currentMajor
        if (comparison == 0) comparison = latestMinor - currentMinor
        if (comparison == 0) comparison = latestPatch - currentPatch

        when {
            comparison > 0 -> printChat {
                +formatTranslated("{module.repository.message.version.update}") {
                    this["current"] = YC.modVersion
                    this["latest"] = latestVersionString
                }
                +updateModNotification
            }

            comparison < 0 -> printChatTranslated("{module.repository.message.version.preview}") {
                this["current"] = YC.modVersion
            }
        }
    }

    fun onTickPre() {
        Repository.options.notifyNewVersion && available && inWorld || return
        notifyNewVersion
    }
}
