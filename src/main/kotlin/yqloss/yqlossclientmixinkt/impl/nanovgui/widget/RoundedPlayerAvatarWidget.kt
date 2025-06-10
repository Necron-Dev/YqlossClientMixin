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

package yqloss.yqlossclientmixinkt.impl.nanovgui.widget

import net.minecraft.client.network.NetworkPlayerInfo
import net.yqloss.uktil.math.Vec2D
import net.yqloss.uktil.math.lerp
import yqloss.yqlossclientmixinkt.impl.nanovgui.NanoVGUIContext
import yqloss.yqlossclientmixinkt.impl.nanovgui.Widget
import yqloss.yqlossclientmixinkt.impl.oneconfiginternal.NanoVGImageCache
import yqloss.yqlossclientmixinkt.impl.oneconfiginternal.nvg
import yqloss.yqlossclientmixinkt.util.MC

data class RoundedPlayerAvatarWidget(
    private val cache: NanoVGImageCache,
    private val profile: NetworkPlayerInfo,
    private val pos: Vec2D,
    private val size: Double,
    private val alpha: Double,
    private val radius: Double,
) : Widget<RoundedPlayerAvatarWidget> {
    override fun draw(context: NanoVGUIContext) {
        context.run {
            MC.textureManager.bindTexture(profile.locationSkin)
            nvg.drawRoundedPlayerAvatar(
                vg,
                cache[profile.gameProfile.id],
                MC.textureManager.getTexture(profile.locationSkin).glTextureId,
                hat = true,
                scaleHat = true,
                pos.x,
                pos.y,
                size,
                size,
                alpha,
                radius,
            )
        }
    }

    override fun alphaScale(alpha: Double) = copy(alpha = this.alpha * alpha)

    override fun scale(
        scale: Double,
        origin: Vec2D,
    ) = copy(pos = lerp(origin, pos, scale), size = size * scale, radius = radius * scale)
}
