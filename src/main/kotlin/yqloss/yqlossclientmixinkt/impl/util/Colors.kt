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

package yqloss.yqlossclientmixinkt.impl.util

import cc.polyfrost.oneconfig.config.core.OneColor

object Colors {
    val NONE = OneColor("00000000")

    val GRAY = listOf(
        "F8F9FAFF",
        "F1F3F5FF",
        "E9ECEFFF",
        "DEE2E6FF",
        "CED4DAFF",
        "ADB5BDFF",
        "868E96FF",
        "495057FF",
        "343A40FF",
        "212529FF",
    ).map(::OneColor)

    val RED = listOf(
        "FFF5F5FF",
        "FFE3E3FF",
        "FFC9C9FF",
        "FFA8A8FF",
        "FF8787FF",
        "FF6B6BFF",
        "FA5252FF",
        "F03E3EFF",
        "E03131FF",
        "C92A2AFF",
    ).map(::OneColor)

    val PINK = listOf(
        "FFF0F6FF",
        "FFDEEBFF",
        "FCC2D7FF",
        "FAA2C1FF",
        "F783ACFF",
        "F06595FF",
        "E64980FF",
        "D6336CFF",
        "C2255CFF",
        "A61E4DFF",
    ).map(::OneColor)

    val GRAPE = listOf(
        "F8F0FCFF",
        "F3D9FAFF",
        "EEBEFAFF",
        "E599F7FF",
        "DA77F2FF",
        "CC5DE8FF",
        "BE4BDBFF",
        "AE3EC9FF",
        "9C36B5FF",
        "862E9CFF",
    ).map(::OneColor)

    val VIOLET = listOf(
        "F3F0FFFF",
        "E5DBFFFF",
        "D0BFFFFF",
        "B197FCFF",
        "9775FAFF",
        "845EF7FF",
        "7950F2FF",
        "7048E8FF",
        "6741D9FF",
        "5F3DC4FF",
    ).map(::OneColor)

    val INDIGO = listOf(
        "EDF2FFFF",
        "DBE4FFFF",
        "BAC8FFFF",
        "91A7FFFF",
        "748FFCFF",
        "5C7CFAFF",
        "4C6EF5FF",
        "4263EBFF",
        "3B5BDBFF",
        "364FC7FF",
    ).map(::OneColor)

    val BLUE = listOf(
        "E7F5FFFF",
        "D0EBFFFF",
        "A5D8FFFF",
        "74C0FCFF",
        "4DABF7FF",
        "339AF0FF",
        "228BE6FF",
        "1C7ED6FF",
        "1971C2FF",
        "1864ABFF",
    ).map(::OneColor)

    val CYAN = listOf(
        "E3FAFCFF",
        "C5F6FAFF",
        "99E9F2FF",
        "66D9E8FF",
        "3BC9DBFF",
        "22B8CFFF",
        "15AABFFF",
        "1098ADFF",
        "0C8599FF",
        "0B7285FF",
    ).map(::OneColor)

    val TEAL = listOf(
        "E6FCF5FF",
        "C3FAE8FF",
        "96F2D7FF",
        "63E6BEFF",
        "38D9A9FF",
        "20C997FF",
        "12B886FF",
        "0CA678FF",
        "099268FF",
        "087F5BFF",
    ).map(::OneColor)

    val GREEN = listOf(
        "EBFBEEFF",
        "D3F9D8FF",
        "B2F2BBFF",
        "8CE99AFF",
        "69DB7CFF",
        "51CF66FF",
        "40C057FF",
        "37B24DFF",
        "2F9E44FF",
        "2B8A3EFF",
    ).map(::OneColor)

    val LIME = listOf(
        "F4FCE3FF",
        "E9FAC8FF",
        "D8F5A2FF",
        "C0EB75FF",
        "A9E34BFF",
        "94D82DFF",
        "82C91EFF",
        "74B816FF",
        "66A80FFF",
        "5C940DFF",
    ).map(::OneColor)

    val YELLOW = listOf(
        "FFF9DBFF",
        "FFF3BFFF",
        "FFEC99FF",
        "FFE066FF",
        "FFD43BFF",
        "FCC419FF",
        "FAB005FF",
        "F59F00FF",
        "F08C00FF",
        "E67700FF",
    ).map(::OneColor)

    val ORANGE = listOf(
        "FFF4E6FF",
        "FFE8CCFF",
        "FFD8A8FF",
        "FFC078FF",
        "FFA94DFF",
        "FF922BFF",
        "FD7E14FF",
        "F76707FF",
        "E8590CFF",
        "D9480FFF",
    ).map(::OneColor)
}
