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
    private val notifyNewVersion by lazy {
        if (YC.modID !in content) {
            printError("\u00A7cversion api: mod ID is not present in response")
        } else if (YC.modVersion != content[YC.modID]) {
            printChat("\u00A7aThere is a new update available for Yqloss Client (Mixin)!")
            printChat("\u00A7aVersion \u00A7e${YC.modVersion} \u00A7a-> \u00A7e${content[YC.modID]}")
            printChat("\u00A7aUpdate the mod on one of the following websites:")
            printURL("https://get.yqloss.net")
            printURL("https://github.com/Necron-Dev/YqlossClientMixin")
        }
    }

    fun onTickPre() {
        Repository.options.notifyNewVersion && available && inWorld || return
        notifyNewVersion
    }
}
