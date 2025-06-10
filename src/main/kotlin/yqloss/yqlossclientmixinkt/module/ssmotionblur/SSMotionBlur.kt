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

package yqloss.yqlossclientmixinkt.module.ssmotionblur

import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.yqloss.uktil.accessor.refs.Mut
import net.yqloss.uktil.accessor.refs.nullMut
import net.yqloss.uktil.accessor.refs.value
import net.yqloss.uktil.accessor.swap
import net.yqloss.uktil.event.EventRegistry
import net.yqloss.uktil.event.register
import net.yqloss.uktil.extension.double
import net.yqloss.uktil.scope.longRet
import org.lwjgl.opengl.GL11.*
import yqloss.yqlossclientmixinkt.event.minecraft.YCRenderEvent
import yqloss.yqlossclientmixinkt.module.YCModuleBase
import yqloss.yqlossclientmixinkt.module.enabled
import yqloss.yqlossclientmixinkt.module.inWorld
import yqloss.yqlossclientmixinkt.module.moduleInfo
import yqloss.yqlossclientmixinkt.util.MC
import yqloss.yqlossclientmixinkt.util.glStateScope
import yqloss.yqlossclientmixinkt.util.mcRenderScope
import java.nio.ByteBuffer
import kotlin.math.max

val INFO_SS_MOTION_BLUR = moduleInfo<SSMotionBlurOptions>("ss_motion_blur", "SS Motion Blur")

object SSMotionBlur : YCModuleBase<SSMotionBlurOptions>(INFO_SS_MOTION_BLUR) {
    private var lastWidth = -1
    private var lastHeight = -1
    private var widthFactor = 1.0
    private var heightFactor = 1.0
    private var lastNanos = System.nanoTime()
    private var textureLast1 = nullMut<Int>()
    private var textureLast2 = nullMut<Int>()
    private var textureAccumulation = nullMut<Int>()

    private fun setupTexture(
        texture: Int,
        width: Int,
        height: Int,
    ) {
        val maxWH = max(width, height)
        var size = 16
        while (size < maxWH) size *= 2

        widthFactor = width.double / size
        heightFactor = height.double / size

        glBindTexture(GL_TEXTURE_2D, texture)

        ByteBuffer.allocateDirect(size * size * 4).let { buffer ->
            glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_RGB,
                size,
                size,
                0,
                GL_RGB,
                GL_UNSIGNED_BYTE,
                buffer,
            )
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        }

        logger.info("created texture $texture ($size*$size) for $width*$height")
    }

    private fun allocate(
        texture: Mut<Int?>,
        allocateTexture: Boolean,
        width: Int,
        height: Int,
    ) {
        texture.value?.let { textureId ->
            GlStateManager.deleteTexture(textureId)
            logger.info("deleted texture $textureId")
            texture.value = null
        }

        lastWidth = width
        lastHeight = height

        if (allocateTexture) {
            val textureId = GlStateManager.generateTexture()
            texture.value = textureId
            setupTexture(textureId, width, height)
        }
    }

    private fun takeScreenShot(
        texture: Mut<Int?>,
        width: Int,
        height: Int,
    ) {
        enabled || return

        if (texture.value === null || lastWidth != width || lastHeight != height) {
            allocate(texture, true, width, height)
        }

        texture.value?.let { textureId ->
            glBindTexture(GL_TEXTURE_2D, textureId)
            glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 0, 0, width, height)
        }
    }

    private fun getAlphas(): AlphaFunction.Alphas {
        val currentNanos = System.nanoTime()
        val diffNanos = currentNanos - lastNanos
        lastNanos = currentNanos
        return options.alphaFunction.calculate(diffNanos.double, options.strength)
    }

    private fun renderMotionBlur(
        screenWidth: Int,
        screenHeight: Int,
        scaledResolution: ScaledResolution,
    ) {
        MC.entityRenderer.setupOverlayRendering()

        glStateScope {
            if (
                textureLast1.value === null ||
                textureLast2.value === null ||
                textureAccumulation.value == null ||
                lastWidth != screenWidth ||
                lastHeight != screenHeight
            ) {
                allocate(textureLast1, true, screenWidth, screenHeight)
                allocate(textureLast2, true, screenWidth, screenHeight)
                allocate(textureAccumulation, true, screenWidth, screenHeight)
            }

            glEnable(GL_TEXTURE_2D)
            glEnable(GL_BLEND)
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
            glDisable(GL_DEPTH_TEST)
            glDisable(GL_ALPHA_TEST)
            glDisable(GL_CULL_FACE)
            glDisable(GL_LIGHTING)

            takeScreenShot(textureLast2, screenWidth, screenHeight)

            fun render(
                textureId: Int?,
                alpha: Double?,
            ) {
                textureId ?: return
                alpha ?: return

                glBindTexture(GL_TEXTURE_2D, textureId)
                glColor4d(1.0, 1.0, 1.0, alpha)

                val scaledWidth = scaledResolution.scaledWidth_double
                val scaledHeight = scaledResolution.scaledHeight_double

                mcRenderScope(GL_QUADS, DefaultVertexFormats.POSITION_TEX) {
                    pos(0.0, scaledHeight, 0.0).tex(0.0, 0.0).endVertex()
                    pos(scaledWidth, scaledHeight, 0.0).tex(widthFactor, 0.0).endVertex()
                    pos(scaledWidth, 0.0, 0.0).tex(widthFactor, heightFactor).endVertex()
                    pos(0.0, 0.0, 0.0).tex(0.0, heightFactor).endVertex()
                }
            }

            val alphas = getAlphas()

            render(textureLast1.value, alphas.lastFrame)
            render(textureAccumulation.value, alphas.accumulation)

            textureLast1 swap textureLast2

            takeScreenShot(textureAccumulation, screenWidth, screenHeight)
        }
    }

    override val registerEvents: EventRegistry.() -> Unit
        get() = {
            super.registerEvents(this)

            register<YCRenderEvent.Render.Pre> {
                inWorld && longRet

                lastNanos = System.nanoTime()
                glStateScope {
                    allocate(textureLast1, false, -1, -1)
                    allocate(textureLast2, false, -1, -1)
                    allocate(textureAccumulation, false, -1, -1)
                }
            }

            register<SSMotionBlurEvent.Render> { event ->
                enabled || longRet

                renderMotionBlur(event.screenWidth, event.screenHeight, event.scaledResolution)
            }
        }
}
