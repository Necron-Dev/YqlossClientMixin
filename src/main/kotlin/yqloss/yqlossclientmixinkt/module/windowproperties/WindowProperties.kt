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

package yqloss.yqlossclientmixinkt.module.windowproperties

import net.yqloss.uktil.accessor.getValue
import net.yqloss.uktil.accessor.outs.inBox
import net.yqloss.uktil.accessor.refs.trigger
import net.yqloss.uktil.event.EventRegistry
import net.yqloss.uktil.event.register
import net.yqloss.uktil.scope.longRet
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.DisplayMode
import yqloss.yqlossclientmixinkt.event.minecraft.YCMinecraftEvent
import yqloss.yqlossclientmixinkt.module.YCModuleBase
import yqloss.yqlossclientmixinkt.module.enabled
import yqloss.yqlossclientmixinkt.module.moduleInfo
import yqloss.yqlossclientmixinkt.util.MC

val INFO_WINDOW_PROPERTIES = moduleInfo<WindowPropertiesOptions>("window_properties", "Window Properties")

object WindowProperties : YCModuleBase<WindowPropertiesOptions>(INFO_WINDOW_PROPERTIES) {
    private val onWindowTitleChange: Unit by trigger(Display::getTitle) {
        val title = Display.getTitle()
        if (title != options.customTitle) originalWindowTitle = title
    }

    private val onWindowTitleOptionChange: Unit by trigger({
        (options.customTitle.takeIf { enabled && options.enableCustomTitle }).inBox
    }) {
        if (enabled && options.enableCustomTitle) {
            Display.setTitle(options.customTitle)
        } else {
            Display.setTitle(originalWindowTitle)
        }
    }

    private var originalWindowTitle = "Minecraft 1.8.9"

    private var originalWindowWidth = 1920

    private var originalWindowHeight = 1080

    private var originalWindowX = -1

    private var originalWindowY = -1

    private var fullscreenMode = false

    private val windowedFullscreen get() = enabled && options.windowedFullscreen && fullscreenMode

    private val borderless get() = enabled && (options.borderlessWindow || windowedFullscreen)

    private val getInitialWindowSize by lazy {
        originalWindowWidth = Display.getWidth()
        originalWindowHeight = Display.getHeight()
    }

    private val onBorderlessStateChange: Unit by trigger(::borderless) {
        val x = Display.getX()
        val y = Display.getY()
        System.setProperty("org.lwjgl.opengl.Window.undecorated", "$borderless")
        Display.setDisplayMode(DisplayMode(Display.getWidth(), Display.getHeight()))
        Display.setLocation(x, y)
        Display.setResizable(false)
        if (!borderless) Display.setResizable(true)
        val grabbed = Mouse.isGrabbed()
        Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2)
        Mouse.setGrabbed(!grabbed)
        Mouse.setGrabbed(grabbed)
    }

    private val onFullscreenStateChange: Unit by trigger(::windowedFullscreen) {
        getInitialWindowSize
        Display.setFullscreen(false)
        if (windowedFullscreen) {
            originalWindowX = Display.getX()
            originalWindowY = Display.getY()
            originalWindowWidth = Display.getWidth()
            originalWindowHeight = Display.getHeight()
            val displayMode = Display.getDesktopDisplayMode()
            val width = if (options.debugHalfFullscreen) displayMode.width / 2 else displayMode.width
            val height = if (options.debugHalfFullscreen) displayMode.height / 2 else displayMode.height
            if (options.disableFullscreenOptimization) {
                Display.setDisplayMode(DisplayMode(width + 2, height + 2))
                if (options.debugHalfFullscreen) {
                    Display.setLocation(-1, -1)
                } else {
                    Display.setLocation(0, -2)
                }
            } else {
                Display.setDisplayMode(DisplayMode(width, height))
                Display.setLocation(0, 0)
            }
            MC.resize(width, height)
        } else {
            Display.setDisplayMode(DisplayMode(originalWindowWidth, originalWindowHeight))
            Display.setResizable(false)
            Display.setResizable(true)
            Display.setLocation(originalWindowX, originalWindowY)
            MC.resize(originalWindowWidth, originalWindowHeight)
        }
        Display.setVSyncEnabled(MC.gameSettings.enableVsync)
        val grabbed = Mouse.isGrabbed()
        Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2)
        Mouse.setGrabbed(!grabbed)
        Mouse.setGrabbed(grabbed)
        MC.updateDisplay()
    }

    private val onEnabledChange by trigger(Unit, false, { enabled }) {
        fullscreenMode = false
        onWindowTitleOptionChange
        onBorderlessStateChange
        onFullscreenStateChange
    }

    override val registerEvents: EventRegistry.() -> Unit
        get() = {
            super.registerEvents(this)

            register<YCMinecraftEvent.Loop.Pre> {
                onEnabledChange

                enabled || longRet

                if (!windowedFullscreen && fullscreenMode) fullscreenMode = false

                onWindowTitleOptionChange
                onWindowTitleChange

                if (enabled && options.enableCustomTitle && Display.getTitle() != options.customTitle) {
                    Display.setTitle(options.customTitle)
                }

                onBorderlessStateChange
                onFullscreenStateChange
            }

            register<WindowPropertiesEvent.Fullscreen> { event ->
                enabled && (fullscreenMode || options.windowedFullscreen) || longRet

                event.canceled = true
                fullscreenMode = !fullscreenMode

                onBorderlessStateChange
                onFullscreenStateChange
            }
        }
}
