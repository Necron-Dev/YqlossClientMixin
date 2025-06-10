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

package yqloss.yqlossclientmixinkt.impl.oneconfiginternal.lwjglmanagerimplloaded

import cc.polyfrost.oneconfig.libs.universal.UGraphics
import cc.polyfrost.oneconfig.platform.Platform
import cc.polyfrost.oneconfig.renderer.font.Font
import net.yqloss.uktil.extension.float
import net.yqloss.uktil.math.convertARGBToDoubleArray
import net.yqloss.uktil.scope.usingScope
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.nanovg.NVGPaint
import org.lwjgl.nanovg.NanoVG.*
import org.lwjgl.nanovg.NanoVGGL2
import org.lwjgl.nanovg.NanoVGGL3.NVG_IMAGE_NODELETE
import org.lwjgl.nanovg.NanoVGGL3.nvglCreateImageFromHandle
import org.lwjgl.opengl.GL11
import yqloss.yqlossclientmixinkt.impl.oneconfiginternal.NanoVGAccessor
import yqloss.yqlossclientmixinkt.impl.oneconfiginternal.NanoVGImageCacheEntry
import yqloss.yqlossclientmixinkt.impl.oneconfiginternal.nvg
import yqloss.yqlossclientmixinkt.util.windowSize
import java.nio.ByteBuffer
import java.util.*
import kotlin.math.*

private fun NVGColor.fill(argb: Int): NVGColor {
    val (rv, gv, bv, av) = convertARGBToDoubleArray(argb)
    r(rv.float)
    g(gv.float)
    b(bv.float)
    a(av.float)
    return this
}

object NanoVGAccessorImpl : NanoVGAccessor {
    private val vgNoAA by lazy { NanoVGGL2.nvgCreate(0) }
    private val vgAA by lazy { NanoVGGL2.nvgCreate(NanoVGGL2.NVG_ANTIALIAS) }

    init {
        nvg = this
    }

    override fun loadFont(
        vg: Long,
        name: String,
    ): Font {
        val byteArray = javaClass.getResource("/assets/yqlossclientmixin/font/$name")!!.readBytes()
        val byteBuffer =
            ByteBuffer.allocateDirect(byteArray.size).apply {
                put(byteArray)
                flip()
            }
        val fontFace = UUID.randomUUID().toString()
        nvgCreateFontMem(vg, fontFace, byteBuffer, 0)
        return object : Font(fontFace, "") {
            val byteBuffer = byteBuffer
        }
    }

    override fun addFallbackFont(
        vg: Long,
        fontFace: String,
        fallbackFontFace: String,
    ) {
        nvgAddFallbackFont(vg, fontFace, fallbackFontFace)
    }

    override fun deleteImages(
        vg: Long,
        images: Set<Int>,
    ) {
        images.forEach {
            nvgDeleteImage(vg, it)
        }
    }

    override fun drawRingArc(
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
    ) {
        usingScope {
            val nvgColor = NVGColor.calloc().using.fill(color)

            val lpo = asin(arcPaddingFrom / outerRadius)
            val lpi = asin(arcPaddingFrom / innerRadius)
            val rpo = asin(arcPaddingTo / outerRadius)
            val rpi = asin(arcPaddingTo / innerRadius)

            nvgBeginPath(vg)
            nvgArc(
                vg,
                x.float,
                y.float,
                outerRadius.float,
                (fromRadian + lpo).float,
                (toRadian - rpo).float,
                NVG_CW,
            )
            nvgArc(
                vg,
                x.float,
                y.float,
                innerRadius.float,
                (toRadian - rpi).float,
                (fromRadian + lpi).float,
                NVG_CCW,
            )
            nvgClosePath(vg)
            nvgFillColor(vg, nvgColor)
            nvgFill(vg)
        }
    }

    private fun drawRoundedImage(
        vg: Long,
        image: Int,
        imageXRel: Double,
        imageYRel: Double,
        imageWRel: Double,
        imageHRel: Double,
        x: Double,
        y: Double,
        width: Double,
        height: Double,
        alpha: Double,
        radius: Double,
    ) {
        usingScope {
            val nvgPaint = NVGPaint.calloc().using
            val imageW = width / imageWRel
            val imageH = height / imageHRel
            val imageX = x - imageW * imageXRel
            val imageY = y - imageH * imageYRel
            nvgImagePattern(
                vg,
                imageX.float,
                imageY.float,
                imageW.float,
                imageH.float,
                0.0F,
                image,
                alpha.float,
                nvgPaint,
            )
            nvgBeginPath(vg)
            nvgRoundedRect(
                vg,
                x.float,
                y.float,
                width.float,
                height.float,
                radius.float,
            )
            nvgFillPaint(vg, nvgPaint)
            nvgFill(vg)
        }
    }

    override fun drawRoundedPlayerAvatar(
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
    ) {
        imageCache.cleanup(this, vg)

        val nvgImage = imageCache.cache.getOrPut(texture) {
            nvglCreateImageFromHandle(vg, texture, 64, 64, NVG_IMAGE_NEAREST or NVG_IMAGE_NODELETE)
                .also { if (it == -1) return@drawRoundedPlayerAvatar }
        }

        drawRoundedImage(
            vg,
            nvgImage,
            0.125,
            0.125,
            0.125,
            0.125,
            x,
            y,
            width,
            height,
            alpha,
            radius,
        )

        if (hat) {
            val offset = if (scaleHat) 1.0 / 144.0 else 0.0

            drawRoundedImage(
                vg,
                nvgImage,
                0.625 + offset,
                0.125 + offset,
                0.125 - 2.0 * offset,
                0.125 - 2.0 * offset,
                x,
                y,
                width,
                height,
                alpha,
                radius,
            )
        }
    }

    override fun drawLines(
        vg: Long,
        points: Iterator<Pair<Double, Double>>,
        radius: Double,
        color: Int,
        alpha: Double,
    ) {
        points.hasNext() || return
        usingScope {
            val nvgColor = NVGColor
                .calloc()
                .using
                .fill(color)
                .a(alpha.float)
            var point = points.next()
            if (points.hasNext()) {
                nvgBeginPath(vg)
                points.forEach {
                    val a = atan2(it.second - point.second, it.first - point.first)
                    nvgMoveTo(
                        vg,
                        (point.first - radius * sin(a)).float,
                        (point.second + radius * cos(a)).float,
                    )
                    nvgArc(
                        vg,
                        point.first.float,
                        point.second.float,
                        radius.float,
                        (a + PI * 0.5).float,
                        (a + PI * 1.5).float,
                        NVG_CW,
                    )
                    nvgArc(
                        vg,
                        it.first.float,
                        it.second.float,
                        radius.float,
                        (a - PI * 0.5).float,
                        (a + PI * 0.5).float,
                        NVG_CW,
                    )
                    nvgLineTo(
                        vg,
                        (point.first - radius * sin(a)).float,
                        (point.second + radius * cos(a)).float,
                    )
                    point = it
                }
                nvgFillColor(vg, nvgColor)
                nvgFill(vg)
            } else {
                nvgBeginPath(vg)
                nvgCircle(vg, point.first.float, point.second.float, radius.float)
                nvgFillColor(vg, nvgColor)
                nvgFill(vg)
            }
        }
    }

    override fun runInNoAAContext(function: (Long) -> Unit) {
        Platform.getGLPlatform().enableStencil()
        GL11.glPushAttrib(1048575)
        UGraphics.disableAlpha()
        nvgBeginFrame(
            vgNoAA,
            windowSize.x.float,
            windowSize.y.float,
            1F,
        )
        function(vgNoAA)
        nvgEndFrame(vgNoAA)
        UGraphics.enableAlpha()
        GL11.glPopAttrib()
    }

    override fun runInAAContext(function: (Long) -> Unit) {
        Platform.getGLPlatform().enableStencil()
        GL11.glPushAttrib(1048575)
        UGraphics.disableAlpha()
        nvgBeginFrame(
            vgAA,
            windowSize.x.float,
            windowSize.y.float,
            1F,
        )
        function(vgAA)
        nvgEndFrame(vgAA)
        UGraphics.enableAlpha()
        GL11.glPopAttrib()
    }
}
