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

package yqloss.yqlossclientmixinkt.impl.oneconfiginternal

import cc.polyfrost.oneconfig.renderer.font.Font
import cc.polyfrost.oneconfig.renderer.font.Fonts
import yqloss.yqlossclientmixinkt.util.accessor.getValue
import yqloss.yqlossclientmixinkt.util.accessor.refs.lateVal
import yqloss.yqlossclientmixinkt.util.accessor.setValue
import yqloss.yqlossclientmixinkt.util.accessor.value

var nvg: NanoVGAccessor by lateVal()

private var fontLoaded = false

val fontMedium = lateVal<Font>()
val fontSemiBold = lateVal<Font>()
var fontChineseRegular: Font by lateVal()
var fontChineseMedium: Font by lateVal()
var fontChineseSemiBold: Font by lateVal()
var fontChineseBold: Font by lateVal()

fun NanoVGAccessor.loadFonts(vg: Long) {
    if (!fontLoaded) {
        fontLoaded = true
        fontMedium.value = loadFont(vg, "montserrat/medium.ttf")
        fontSemiBold.value = loadFont(vg, "montserrat/semibold.ttf")
        fontChineseRegular = loadFont(vg, "notosans_sc/regular.ttf")
        fontChineseMedium = loadFont(vg, "notosans_sc/medium.ttf")
        fontChineseSemiBold = loadFont(vg, "notosans_sc/semibold.ttf")
        fontChineseBold = loadFont(vg, "notosans_sc/bold.ttf")
        addFallbackFont(vg, fontMedium.value.name, fontChineseMedium.name)
        addFallbackFont(vg, fontSemiBold.value.name, fontChineseSemiBold.name)
        addFallbackFont(vg, Fonts.REGULAR.name, fontChineseRegular.name)
        addFallbackFont(vg, Fonts.MEDIUM.name, fontChineseMedium.name)
        addFallbackFont(vg, Fonts.SEMIBOLD.name, fontChineseSemiBold.name)
        addFallbackFont(vg, Fonts.BOLD.name, fontChineseBold.name)
        addFallbackFont(vg, Fonts.MINECRAFT_REGULAR.name, fontChineseRegular.name)
        addFallbackFont(vg, Fonts.MINECRAFT_BOLD.name, fontChineseBold.name)
    }
}

class NanoVGImageCache {
    private val cache = mutableMapOf<Any?, MutableMap<Int, Int>>()
    private val imagesToDelete = mutableSetOf<Int>()

    operator fun get(key: Any?) = NanoVGImageCacheEntry(this, cache.getOrPut(key, ::mutableMapOf))

    fun cleanup(
        accessor: NanoVGAccessor,
        vg: Long,
    ) {
        accessor.deleteImages(vg, imagesToDelete)
        imagesToDelete.clear()
    }

    fun clear() {
        cache.values.forEach { entry ->
            entry.values.forEach(imagesToDelete::add)
        }
        cache.clear()
    }

    fun clear(key: Any?) {
        cache[key]?.values?.forEach(imagesToDelete::add)
        cache.remove(key)
    }
}

class NanoVGImageCacheEntry(
    val parent: NanoVGImageCache,
    val cache: MutableMap<Int, Int>,
) {
    fun cleanup(
        accessor: NanoVGAccessor,
        vg: Long,
    ) {
        parent.cleanup(accessor, vg)
    }
}

interface NanoVGAccessor {
    fun loadFont(
        vg: Long,
        name: String,
    ): Font

    fun addFallbackFont(
        vg: Long,
        fontFace: String,
        fallbackFontFace: String,
    )

    fun deleteImages(
        vg: Long,
        images: Set<Int>,
    )

    fun drawRingArc(
        vg: Long,
        x: Double,
        y: Double,
        outerRadius: Double,
        innerRadius: Double,
        fromRadian: Double,
        toRadian: Double,
        arcPaddingFrom: Double,
        arcPaddingTo: Double,
        color: Int,
    )

    fun drawRoundedPlayerAvatar(
        vg: Long,
        imageCache: NanoVGImageCacheEntry,
        texture: Int,
        hat: Boolean,
        scaleHat: Boolean,
        x: Double,
        y: Double,
        width: Double,
        height: Double,
        alpha: Double,
        radius: Double,
    )

    fun drawLines(
        vg: Long,
        points: Iterator<Pair<Double, Double>>,
        radius: Double,
        color: Int,
        alpha: Double,
    )

    fun runInNoAAContext(function: (Long) -> Unit)
}
