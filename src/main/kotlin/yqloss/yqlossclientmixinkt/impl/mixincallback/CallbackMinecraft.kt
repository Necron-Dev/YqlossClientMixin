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

package yqloss.yqlossclientmixinkt.impl.mixincallback

import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.util.BlockPos
import net.yqloss.uktil.extension.double
import net.yqloss.uktil.math.Vec2D
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import yqloss.yqlossclientmixinkt.YC
import yqloss.yqlossclientmixinkt.event.minecraft.YCMinecraftEvent
import yqloss.yqlossclientmixinkt.event.minecraft.YCRenderEvent
import yqloss.yqlossclientmixinkt.module.miningprediction.MiningPredictionEvent
import yqloss.yqlossclientmixinkt.module.tweaks.TweaksEvent
import yqloss.yqlossclientmixinkt.module.windowproperties.WindowPropertiesEvent
import yqloss.yqlossclientmixinkt.util.*

object CallbackMinecraft {
    object YqlossClient {
        fun startGamePost() {
            YC.eventDispatcher(YCMinecraftEvent.Load.Post)
        }

        fun runGameLoopPre() {
            ++frameCounter
            mousePosition = Vec2D(
                Mouse.getX().double,
                MC.displayHeight - Mouse.getY() - 1.0,
            )
            windowSize = Vec2D(MC.displayWidth.double, MC.displayHeight.double)
            guiScale = ScaledResolution(MC).scaleFactor.double
            YC.eventDispatcher(YCMinecraftEvent.Loop.Pre)
        }

        fun runTickPre() {
            ++tickCounter
            YC.eventDispatcher(YCMinecraftEvent.Tick.Pre)
        }

        fun runTickPost() {
            YC.eventDispatcher(YCMinecraftEvent.Tick.Post)
        }

        fun loadWorldPre(world: WorldClient?) {
            YC.eventDispatcher(YCMinecraftEvent.LoadWorld.Pre(world))
        }

        fun runTickHandleInput(screen: GuiScreen) {
            YCRenderEvent.Screen
                .Proxy(screen)
                .also(YC.eventDispatcher)
                .mutableScreen
                ?.run {
                    if (Mouse.isCreated()) {
                        while (Mouse.next()) {
                            handleMouseInput()
                        }
                    }
                    if (Keyboard.isCreated()) {
                        while (Keyboard.next()) {
                            handleKeyboardInput()
                        }
                    }
                    handleInput()
                }
                ?: screen.handleInput()
        }

        fun runTickHandleKeyboardInput(screen: GuiScreen) {
            val proxiedScreen = YCRenderEvent.Screen
                .Proxy(screen)
                .also(YC.eventDispatcher)
                .mutableScreen
                ?: screen
            proxiedScreen.handleKeyboardInput()
        }

        fun runTickHandleMouseInput(screen: GuiScreen) {
            val proxiedScreen = YCRenderEvent.Screen
                .Proxy(screen)
                .also(YC.eventDispatcher)
                .mutableScreen
                ?: screen
            proxiedScreen.handleMouseInput()
        }
    }

    object Tweaks {
        fun rightClickMouseClickBlockPre(
            world: WorldClient,
            blockPos: BlockPos,
        ) = TweaksEvent
            .RightClickBlockPre(world, blockPos.asVec3I)
            .also(YC.eventDispatcher)
            .canceled
    }

    object MiningPrediction {
        fun sendClickBlockToControllerMining(blockPos: BlockPos) {
            YC.eventDispatcher(MiningPredictionEvent.Mining(blockPos.asVec3I))
        }

        fun sendClickBlockToControllerNotMining() {
            YC.eventDispatcher(MiningPredictionEvent.NotMining)
        }
    }

    object WindowProperties {
        fun toggleFullscreenPre(ci: CallbackInfo) {
            WindowPropertiesEvent
                .Fullscreen()
                .also(YC.eventDispatcher)
                .apply {
                    if (canceled) {
                        ci.cancel()
                    }
                }
        }
    }
}
