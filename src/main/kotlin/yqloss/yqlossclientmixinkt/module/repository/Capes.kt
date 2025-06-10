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

import kotlinx.serialization.Serializable
import net.minecraft.util.ResourceLocation
import net.yqloss.uktil.accessor.outs.value
import net.yqloss.uktil.extension.type.ifTake
import net.yqloss.uktil.extension.type.undashedLowerString
import yqloss.yqlossclientmixinkt.network.*
import yqloss.yqlossclientmixinkt.util.relativeURL
import java.awt.image.BufferedImage
import java.util.*

const val URL_CAPES = "http://ycm.yqloss.net/capes.json"

typealias CapesData = Map<String, Capes.Cape>

class Capes : TypedResource<CapesData> by CooldownTypedResource(JsonResource(URL_CAPES), Repository.options.capeCooldown) {
    @Serializable
    data class Cape(
        val metadata: String,
    )

    private val capeCache = mutableMapOf<UUID, CapeMeta>()
    private val imageCache = mutableMapOf<String, TypedResource<BufferedImage>>()

    fun addToImageCache(urls: Iterable<String>) {
        synchronized(imageCache) {
            urls.forEach {
                if (it !in imageCache) {
                    imageCache[it] = CooldownTypedResource(ImageResource(it), Repository.options.capeTextureCooldown)
                }
            }
        }
    }

    fun getImageCache(url: String): BufferedImage? {
        synchronized(imageCache) {
            return imageCache[url]?.data?.value
        }
    }

    fun onTickPre() {
        capeCache.values.requestAll()
        synchronized(imageCache) {
            imageCache.values.requestAll()
        }
    }

    fun onLoadCape(uuid: UUID): ResourceLocation? {
        if (!Repository.options.showModCapes || !available) return null
        capeCache[uuid]?.let { return it.available.ifTake { it.texture } }
        val uuidString = uuid.undashedLowerString
        val rel = (content[uuidString] ?: return null).metadata
        capeCache[uuid] = CapeMeta(relativeURL(URL_CAPES, rel), this)
        return null
    }
}
