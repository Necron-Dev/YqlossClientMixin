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

package yqloss.yqlossclientmixinkt.util

fun relativeURL(
    base: String,
    path: String,
): String {
    return when {
        path.startsWith('/') -> {
            val indexProtocol = base.indexOf("://")
            indexProtocol != -1 || return path
            var indexRoot = base.indexOf('/', indexProtocol + 3)
            if (indexRoot == -1) indexRoot = base.length
            base.substring(0..<indexRoot) + path
        }

        path.startsWith('.') -> {
            val dotRemoved = path.trimStart('.')
            dotRemoved.startsWith('/') || return path
            val dotCount = path.length - dotRemoved.length
            var cursor = base.length
            repeat(dotCount - 1) {
                val indexSlash = base.lastIndexOf('/', cursor - 1)
                indexSlash != -1 || return path
                cursor = indexSlash
            }
            base.substring(0..<cursor) + dotRemoved
        }

        else -> path
    }
}
