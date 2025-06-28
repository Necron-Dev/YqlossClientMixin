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

package yqloss.yqlossclientmixinkt.module.extensions

data class ExtensionIdentifier(
    val author: String,
    val name: String,
    val branch: String,
    val version: Int,
    val repository: String,
) {
    companion object {
        private val REGEX =
            Regex("^(?:([0-9A-Za-z_]+)\\.)?([0-9A-Za-z_]+)(?:-([0-9A-Za-z_]+))?(?::([1-9][0-9]*))?(?:@(official(?:_[0-9A-Za-z_]+)?|github|https?://(?:[A-Za-z0-9\\-]+\\.)+[A-Za-z0-9\\-]+(?::0|:[1-9][0-9]{0,3}|:[1-5][0-9]{4}|:6[0-4][0-9]{3}|:65[0-4][0-9]{2}|:655[0-2][0-9]|:6553[0-5])?(/[A-Za-z0-9_\\-.~]+)*))?$")

        fun fromString(string: String): ExtensionIdentifier? {
            return null
        }
    }
}
