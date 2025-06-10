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

package yqloss.yqlossclientmixinkt.impl.module.ycleapmenu

import net.yqloss.uktil.accessor.getValue
import net.yqloss.uktil.accessor.outs.Box
import net.yqloss.uktil.accessor.outs.inBox
import net.yqloss.uktil.accessor.outs.value
import net.yqloss.uktil.accessor.refs.inMut
import net.yqloss.uktil.accessor.refs.trigger
import net.yqloss.uktil.accessor.refs.value
import net.yqloss.uktil.event.EventRegistry
import net.yqloss.uktil.event.register
import net.yqloss.uktil.extension.sameNotNull
import net.yqloss.uktil.math.Vec2D
import net.yqloss.uktil.math.blendColor
import net.yqloss.uktil.math.unitVec
import net.yqloss.uktil.scope.longRet
import yqloss.yqlossclientmixinkt.event.minecraft.YCInputEvent
import yqloss.yqlossclientmixinkt.event.minecraft.YCMinecraftEvent
import yqloss.yqlossclientmixinkt.impl.module.YCModuleScreenBase
import yqloss.yqlossclientmixinkt.impl.nanovgui.Transformation
import yqloss.yqlossclientmixinkt.impl.nanovgui.Widget
import yqloss.yqlossclientmixinkt.impl.nanovgui.gui.EllipseButton
import yqloss.yqlossclientmixinkt.impl.nanovgui.gui.Fade
import yqloss.yqlossclientmixinkt.impl.nanovgui.gui.RingArcButton
import yqloss.yqlossclientmixinkt.impl.nanovgui.widget.RingArcWidget
import yqloss.yqlossclientmixinkt.impl.nanovgui.widget.RoundedPlayerAvatarWidget
import yqloss.yqlossclientmixinkt.impl.nanovgui.widget.RoundedRectWidget
import yqloss.yqlossclientmixinkt.impl.nanovgui.widget.TextWidget
import yqloss.yqlossclientmixinkt.impl.oneconfiginternal.NanoVGImageCache
import yqloss.yqlossclientmixinkt.impl.oneconfiginternal.fontMedium
import yqloss.yqlossclientmixinkt.impl.option.handle
import yqloss.yqlossclientmixinkt.impl.option.module.YCLeapMenuOptionsImpl
import yqloss.yqlossclientmixinkt.impl.util.Colors
import yqloss.yqlossclientmixinkt.module.register
import yqloss.yqlossclientmixinkt.module.ycleapmenu.YCLeapMenu
import yqloss.yqlossclientmixinkt.util.MC
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.tan

private const val PADDING_ARC = 1.0
private const val PADDING_DEFAULT = 2.0
private val OFFSET_PREFERRED = Vec2D(-55.0 + PADDING_DEFAULT, -55.0 + PADDING_DEFAULT)

object YCLeapMenuScreen : YCModuleScreenBase<YCLeapMenuOptionsImpl, YCLeapMenu>(YCLeapMenu) {
    override val width = 0.0

    override val height = 0.0

    override val scaleOverride get() = options.scaleOverride.nullableScale

    private val cache = NanoVGImageCache()

    private var classButtons: List<ClassLeapButton>? = null

    private var preferredButton: PreferredLeapButton? = null

    private val hidden by trigger({ MC.currentScreen }) { false.inMut }

    override val ensureShow get() = YCLeapMenu.Screen.proxiedScreen sameNotNull MC.currentScreen && !hidden.value

    override fun reset() {
        classButtons = null
        preferredButton = null
        cache.clear()
        module.clearDeadMap()
    }

    override fun draw(
        widgets: MutableList<Widget<*>>,
        box: Vec2D,
        tr: Transformation,
    ) {
        val classButtons =
            classButtons ?: List(5) {
                object : ClassLeapButton(it) {
                }
            }

        val preferredButton = preferredButton ?: PreferredLeapButton()

        this.classButtons = classButtons
        this.preferredButton = preferredButton

        repeat(5) {
            classButtons[it].info = module.getPlayerInfo(it)
        }

        var updateCenter = false
        var centerInfo: YCLeapMenu.PlayerInfo? = null

        if (preferredButton.isActuallyHovered(tr + OFFSET_PREFERRED)) {
            centerInfo = module.getPlayerInfo(-1)
            updateCenter = true
        }

        repeat(5) {
            val offsetRadian = (it * 0.4 - 0.7) * PI
            val offset = unitVec(offsetRadian) * PADDING_ARC / sin(0.2 * PI)
            classButtons[it].render(widgets, tr + offset)
            if (classButtons[it].isHovered(tr + offset)) {
                centerInfo = classButtons[it].info
                updateCenter = true
            }
        }

        if (updateCenter) {
            preferredButton.info.switch(centerInfo.inBox)
        }

        preferredButton.render(widgets, tr + OFFSET_PREFERRED)
    }

    fun clickButton(index: Int) {
        classButtons?.get(index)?.onMouseDown(0)
    }

    override val registerEvents: EventRegistry.() -> Unit = {
        super.registerEvents(this)

        register<YCInputEvent.Mouse.Click> { event ->
            event.screen && ensureShow || longRet

            val tr = transformation

            classButtons?.withIndex()?.firstOrNull { (i, button) ->
                val offsetRadian = (i * 0.4 - 0.7) * PI
                val offset = unitVec(offsetRadian) * PADDING_ARC / sin(0.2 * PI)
                if (button.isHovered(tr + offset)) {
                    button.onMouseDown(event.button)
                    true
                } else {
                    false
                }
            } ?: run {
                if (preferredButton?.isHovered(tr + OFFSET_PREFERRED) == true) {
                    preferredButton?.onMouseDown(event.button)
                }
            }
        }

        register<YCMinecraftEvent.Loop.Pre> { event ->
            options.keyBinds.forEach { it.handle() }
        }
    }

    abstract class ClassLeapButton(
        private val index: Int,
    ) : RingArcButton<YCLeapMenu.PlayerInfo?>(null) {
        override val smooth get() = options.smoothGUI

        override val outerRadius = 120.0 - PADDING_ARC / tan(0.2 * PI)
        override val innerRadius = 55.0 - PADDING_ARC / tan(0.2 * PI)
        private val extraRadius = 115.0 - PADDING_ARC / tan(0.2 * PI)
        override val fromRadian = (index * 0.4 - 0.9) * PI
        override val toRadian = (index * 0.4 - 0.5) * PI

        override fun getArcPadding(hovered: Boolean) = 0.0

        override fun getColor(hovered: Boolean) = Colors.GRAY[9].rgb

        override fun getSize(hovered: Boolean): Double {
            return if (hovered) {
                if (isAnyDown()) 1.0 else 1.05
            } else {
                1.0
            }
        }

        override fun isHovered(tr: Transformation): Boolean {
            return if (info?.dead == false) {
                super.isHovered(tr)
            } else {
                false
            }
        }

        override fun renderBackground(
            widgets: MutableList<Widget<*>>,
            tr: Transformation,
            hovered: Boolean,
        ) {
            val player = info
            val padding = getArcPadding(hovered)
            if (player === null) {
                widgets.add(
                    RingArcWidget(
                        tr pos Vec2D(0.0, 0.0),
                        tr size outerRadius,
                        tr size extraRadius,
                        fromRadian,
                        toRadian,
                        tr size padding,
                        tr size padding,
                        Colors.GRAY[9].rgb,
                    ),
                )
            } else {
                super.renderBackground(widgets, tr, hovered)
                if (player.dead) {
                    widgets.add(
                        RingArcWidget(
                            tr pos Vec2D(0.0, 0.0),
                            tr size outerRadius,
                            tr size extraRadius,
                            fromRadian,
                            toRadian,
                            tr size padding,
                            tr size padding,
                            Colors.RED[8].rgb,
                        ),
                    )
                }
            }
        }

        override fun renderIcon(
            widgets: MutableList<Widget<*>>,
            tr: Transformation,
            hovered: Boolean,
        ) {
            val info = info ?: return

            val ttr = tr + unitVec((0.4 * index - 0.7) * PI) * 85.0

            widgets.add(
                RoundedPlayerAvatarWidget(
                    cache,
                    info.profile,
                    ttr pos Vec2D(-12.0, -20.0),
                    tr size 24.0,
                    1.0,
                    tr size 8.0,
                ),
            )

            if (info.dead) {
                widgets.add(
                    RoundedRectWidget(
                        ttr pos Vec2D(-12.0, -20.0),
                        ttr pos Vec2D(12.0, 4.0),
                        0x7F000000,
                        tr size 8.0,
                    ),
                )
            }

            widgets.add(
                TextWidget(
                    info.theClass.displayName,
                    ttr pos Vec2D(0.0, 8.0),
                    if (info.dead) {
                        Colors.GRAY[3].rgb blendColor 0x7F000000
                    } else {
                        Colors.GRAY[3].rgb
                    },
                    ttr size 12.0,
                    fontMedium,
                    Vec2D(0.5, 0.0),
                ),
            )
        }

        override fun onMouseDown(button: Int) {
            info?.let {
                module.leapTo(it.profile.gameProfile.name)
                hidden.value = true
            }
        }
    }

    class LeapFade : Fade<Box<YCLeapMenu.PlayerInfo?>?>(null) {
        override val smooth get() = options.smoothGUI

        override fun renderSingle(
            widgets: MutableList<Widget<*>>,
            tr: Transformation,
            info: Box<YCLeapMenu.PlayerInfo?>?,
            progress: Double,
            isLast: Boolean,
        ) {
            info ?: return
            val info = info.value
            if (info === null) {
                widgets.add(
                    TextWidget(
                        "No Target!",
                        tr pos Vec2D(0.0, 0.0),
                        Colors.GRAY[3].rgb blendColor 0x7F000000,
                        tr size 12.0,
                        fontMedium,
                        Vec2D(0.5, 0.5),
                    ).alphaScale(progress),
                )
            } else {
                widgets.add(
                    RoundedPlayerAvatarWidget(
                        cache,
                        info.profile,
                        tr pos Vec2D(-12.0, -12.0),
                        tr size 24.0,
                        1.0,
                        tr size 8.0,
                    ).alphaScale(progress),
                )

                widgets.add(
                    TextWidget(
                        info.theClass.displayName,
                        tr pos Vec2D(0.0, 16.0),
                        Colors.GRAY[3].rgb,
                        tr size 12.0,
                        fontMedium,
                        Vec2D(0.5, 0.0),
                    ).alphaScale(progress),
                )

                widgets.add(
                    TextWidget(
                        info.profile.gameProfile.name,
                        tr pos Vec2D(0.0, -16.0),
                        Colors.GRAY[3].rgb,
                        tr size 12.0,
                        fontMedium,
                        Vec2D(0.5, 1.0),
                        tr size 90.0,
                        "...",
                    ).alphaScale(progress),
                )
            }
        }
    }

    class PreferredLeapButton : EllipseButton<LeapFade>(LeapFade()) {
        override val smooth get() = options.smoothGUI

        override val collisionSize = Vec2D(110.0 - PADDING_DEFAULT * 2, 110.0 - PADDING_DEFAULT * 2)

        override fun getColor(hovered: Boolean) = Colors.GRAY[9].rgb

        override fun getSize(hovered: Boolean): Double {
            return if (hovered) {
                if (isAnyDown()) 0.95 else 1.0
            } else {
                1.0
            }
        }

        fun isActuallyHovered(tr: Transformation) = super.isHovered(tr)

        override fun isHovered(tr: Transformation) = info.curr?.value !== null && super.isHovered(tr)

        override fun renderIcon(
            widgets: MutableList<Widget<*>>,
            tr: Transformation,
            hovered: Boolean,
        ) {
            info.render(widgets, tr)
        }

        override fun onMouseDown(button: Int) {
            info.curr?.value?.profile?.let {
                module.leapTo(it.gameProfile.name)
                hidden.value = true
            }
        }
    }

    init {
        register
    }
}
