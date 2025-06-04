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
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import yqloss.yqlossclientmixinkt.network.CooldownTypedResource
import yqloss.yqlossclientmixinkt.network.JsonResource
import yqloss.yqlossclientmixinkt.network.TypedResource
import yqloss.yqlossclientmixinkt.network.content
import yqloss.yqlossclientmixinkt.util.CAPE_SWITCH_MAX_DEPTH
import yqloss.yqlossclientmixinkt.util.MC
import yqloss.yqlossclientmixinkt.util.accessor.getValue
import yqloss.yqlossclientmixinkt.util.accessor.refs.lateVar
import yqloss.yqlossclientmixinkt.util.accessor.setValue
import yqloss.yqlossclientmixinkt.util.extension.long
import yqloss.yqlossclientmixinkt.util.extension.type.undashedLowerString
import yqloss.yqlossclientmixinkt.util.functional.loop
import yqloss.yqlossclientmixinkt.util.functional.plus
import yqloss.yqlossclientmixinkt.util.relativeURL
import java.awt.image.BufferedImage
import java.util.*
import kotlin.random.Random

class CapeMeta(
    private val url: String,
    private val capes: Capes,
) : TypedResource<CapeMeta.Data> by CooldownTypedResource(JsonResource(url), Repository.options.capeMetadataCooldown) {
    @Serializable
    data class Texture(
        val path: String,
        val xCount: Int = 1,
        val yCount: Int = 1,
        val x: Int = 0,
        val y: Int = 0,
    )

    @Serializable
    data class Edge(
        val weight: Int = 1,
        val texture: String? = null,
        val durationMin: Double? = null,
        val durationMax: Double? = null,
        val next: Map<String, Edge>? = null,
    )

    @Serializable
    data class Node(
        val texture: String? = null,
        val durationMin: Double? = null,
        val durationMax: Double? = null,
        val next: Map<String, Edge> = mapOf(),
    )

    @Serializable
    data class Data(
        val textures: Map<String, Texture> = mapOf(),
        val graph: Map<String, Node> = mapOf(),
    )

    data class CurrentNode(
        val texture: String? = null,
        val duration: Double? = null,
        val next: Map<String, Edge>,
    )

    class BufferedImageTexture(
        private val image: BufferedImage,
    ) : AbstractTexture() {
        override fun loadTexture(resourceManager: IResourceManager) {
            deleteGlTexture()
            TextureUtil.uploadTextureImage(getGlTextureId(), image)
        }
    }

    init {
        onTypedAvailable += { content ->
            capes.addToImageCache(
                buildList {
                    content.textures.forEach { name, it ->
                        val imageURL = relativeURL(url, it.path)
                        urlCache[name] = imageURL
                        add(imageURL)
                    }
                },
            )
            currentNode = content.begin
            switch(content)
        }
    }

    var currentNode: CurrentNode by lateVar()

    var lastSwitchNanos = System.nanoTime()

    val urlCache = mutableMapOf<String, String>()

    val imageCache = mutableMapOf<String, BufferedImage>()

    val resourceLocationCache = mutableMapOf<String, ResourceLocation>()

    private val Data.begin: CurrentNode
        get() {
            val node = this.graph["begin"]!!
            return CurrentNode(
                null,
                null,
                node.next,
            )
        }

    private fun switch(content: Data) {
        val node = currentNode
        if (node.next.isEmpty()) {
            currentNode = content.begin
            switch(content)
            return
        }
        var r = Random.nextInt(node.next.values.sumOf { it.weight })
        val next =
            node.next.entries.first {
                r -= it.value.weight
                r < 0
            }
        val nextNode = content.graph[next.key]!!
        val durationMin = next.value.durationMin ?: nextNode.durationMin
        val durationMax = next.value.durationMax ?: nextNode.durationMax
        val texture = next.value.texture ?: nextNode.texture
        val duration =
            if (durationMin !== null && durationMax !== null) {
                if (durationMin >= durationMax) {
                    durationMin
                } else {
                    Random.nextDouble(durationMin, durationMax)
                }
            } else {
                null
            }
        currentNode =
            CurrentNode(
                texture,
                duration,
                next.value.next ?: nextNode.next,
            )
        lastSwitchNanos = System.nanoTime()
        if (texture === null || duration === null) switch(content)
    }

    private fun switchToPresent() {
        val time = System.nanoTime()
        var counter = CAPE_SWITCH_MAX_DEPTH
        loop {
            val duration = currentNode.duration!!
            if (duration < 0.0) return
            if (counter-- == 0) {
                lastSwitchNanos = time
                return
            }
            val incrementNanos = (duration * 1e9).long
            if (time - lastSwitchNanos < incrementNanos) return
            switch(content)
            lastSwitchNanos += incrementNanos
        }
    }

    val texture: ResourceLocation?
        get() {
            switchToPresent()
            val texture = currentNode.texture ?: return null
            val resourceLocation = resourceLocationCache[texture]
            if (MC.textureManager.getTexture(resourceLocation) is BufferedImageTexture) {
                return resourceLocation
            }
            val image =
                imageCache[texture] ?: run {
                    val imageURL = urlCache[texture] ?: return null
                    val fullImage = capes.getImageCache(imageURL) ?: return null
                    val textureMeta = content.textures[texture] ?: return null
                    val blockW = fullImage.width / textureMeta.xCount
                    val blockH = fullImage.height / textureMeta.yCount
                    val subImage = fullImage.getSubimage(textureMeta.x * blockW, textureMeta.y * blockH, blockW, blockH)
                    imageCache[texture] = subImage
                    subImage
                }
            val newLocation = ResourceLocation("yqlossclientmixin/capes/${UUID.randomUUID().undashedLowerString}")
            MC.textureManager.loadTexture(newLocation, BufferedImageTexture(image))
            resourceLocationCache[texture] = newLocation
            return newLocation
        }
}
