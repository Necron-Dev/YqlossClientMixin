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

package yqloss.yqlossclientmixinkt.module.tweaks

import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.init.Items
import net.yqloss.uktil.event.EventRegistry
import net.yqloss.uktil.event.register
import net.yqloss.uktil.extension.equalsNotNull
import net.yqloss.uktil.scope.longRet
import yqloss.yqlossclientmixinkt.module.YCModuleBase
import yqloss.yqlossclientmixinkt.module.enabled
import yqloss.yqlossclientmixinkt.module.inWorld
import yqloss.yqlossclientmixinkt.module.moduleInfo
import yqloss.yqlossclientmixinkt.util.MC
import yqloss.yqlossclientmixinkt.util.SKYBLOCK_MINING_TOOLS
import yqloss.yqlossclientmixinkt.util.skyBlockUUID

val INFO_TWEAKS = moduleInfo<TweaksOptions>("tweaks", "Tweaks")

object Tweaks : YCModuleBase<TweaksOptions>(INFO_TWEAKS) {
    override val registerEvents: EventRegistry.() -> Unit
        get() = {
            super.registerEvents(this)

            register<TweaksEvent.SetAnglesPost> { (entity) ->
                enabled && options.enableInstantAim || longRet

                if (entity is EntityPlayerSP) {
                    entity.prevRotationYawHead = entity.prevRotationYaw
                    entity.rotationYawHead = entity.rotationYaw
                }
            }

            register<TweaksEvent.RightClickBlockPre> { event ->
                !event.canceled && enabled && options.disablePearlClickBlock && inWorld || longRet

                if (MC.thePlayer.inventory.getCurrentItem()?.item === Items.ender_pearl) event.canceled = true
            }

            register<TweaksEvent.IsHittingPositionCheck> { event ->
                !event.canceled && enabled && options.disableSkyBlockToolsNBTUpdateResetDigging && inWorld || longRet

                val heldItemStack = MC.thePlayer.heldItem
                if (event.currentItemHittingBlock !== null &&
                    heldItemStack !== null &&
                    heldItemStack.item in SKYBLOCK_MINING_TOOLS &&
                    heldItemStack.item === event.currentItemHittingBlock.item &&
                    heldItemStack.skyBlockUUID equalsNotNull event.currentItemHittingBlock.skyBlockUUID
                ) {
                    event.canceled = true
                    event.returnValue = event.pos !== null && event.pos == event.currentBlock
                }
            }
        }
}
