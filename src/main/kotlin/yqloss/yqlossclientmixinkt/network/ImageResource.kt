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

package yqloss.yqlossclientmixinkt.network

import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.yqloss.uktil.scope.usingScope
import okhttp3.Request
import okhttp3.coroutines.executeAsync
import yqloss.yqlossclientmixinkt.YCHttp
import yqloss.yqlossclientmixinkt.util.LOG_NETWORK_ACTIVITY
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

@OptIn(ExperimentalCoroutinesApi::class)
open class ImageResource(
    url: String,
) : SuspendTypedResource<BufferedImage>({
    usingScope {
        if (LOG_NETWORK_ACTIVITY) networkLogger.info("ImageResource requesting: $url")
        val response = YCHttp.newCall(Request.Builder().url(url).build()).executeAsync().using
        if (LOG_NETWORK_ACTIVITY) networkLogger.info("ImageResource response code ${response.code}: $url")
        response.code == 200 || throw Exception("response code ${response.code}: $url")
        ImageIO.read(response.body.byteStream().using).also {
            if (LOG_NETWORK_ACTIVITY) networkLogger.info("ImageResource decoding success: $url")
        }
    }
})
