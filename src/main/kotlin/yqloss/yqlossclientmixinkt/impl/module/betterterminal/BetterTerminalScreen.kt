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

package yqloss.yqlossclientmixinkt.impl.module.betterterminal

import net.yqloss.uktil.event.EventRegistry
import net.yqloss.uktil.event.register
import net.yqloss.uktil.extension.double
import net.yqloss.uktil.extension.sameNotNull
import net.yqloss.uktil.extension.type.ifTake
import net.yqloss.uktil.math.Vec2D
import net.yqloss.uktil.scope.longRet
import org.lwjgl.input.Mouse
import yqloss.yqlossclientmixinkt.event.minecraft.YCInputEvent
import yqloss.yqlossclientmixinkt.impl.module.YCModuleScreenBase
import yqloss.yqlossclientmixinkt.impl.nanovgui.Transformation
import yqloss.yqlossclientmixinkt.impl.nanovgui.Widget
import yqloss.yqlossclientmixinkt.impl.nanovgui.gui.Button
import yqloss.yqlossclientmixinkt.impl.nanovgui.gui.Fade
import yqloss.yqlossclientmixinkt.impl.nanovgui.widget.TextWidget
import yqloss.yqlossclientmixinkt.impl.oneconfiginternal.fontMedium
import yqloss.yqlossclientmixinkt.impl.oneconfiginternal.fontSemiBold
import yqloss.yqlossclientmixinkt.impl.option.module.BetterTerminalOptionsImpl
import yqloss.yqlossclientmixinkt.impl.util.Colors
import yqloss.yqlossclientmixinkt.module.betterterminal.BetterTerminal
import yqloss.yqlossclientmixinkt.module.betterterminal.SlotType
import yqloss.yqlossclientmixinkt.module.betterterminal.terminal.*
import yqloss.yqlossclientmixinkt.util.MC

object BetterTerminalScreen : YCModuleScreenBase<BetterTerminalOptionsImpl, BetterTerminal>(BetterTerminal) {
    private val colorGetterMap = mapOf(
        SlotType.EMPTY to { null },
        SlotType.ORDER_1 to { options.order1 },
        SlotType.ORDER_2 to { options.order2 },
        SlotType.ORDER_3 to { options.order3 },
        SlotType.ORDER_CLICKED to { options.orderClicked },
        SlotType.ORDER_OTHER to { options.orderOther },
        SlotType.PANES_ON to { options.panesOn },
        SlotType.PANES_OFF to { options.panesOff },
        SlotType.START_CORRECT to { options.startAnswer },
        SlotType.START_CLICKED to { options.startClicked },
        SlotType.START_WRONG to { options.startOther },
        SlotType.COLOR_CORRECT to { options.colorAnswer },
        SlotType.COLOR_CLICKED to { options.colorClicked },
        SlotType.COLOR_WRONG to { options.colorOther },
        SlotType.RUBIX_RIGHT_2 to { options.rubixRight2 },
        SlotType.RUBIX_RIGHT_1 to { options.rubixRight1 },
        SlotType.RUBIX_CORRECT to { options.rubix0 },
        SlotType.RUBIX_LEFT_1 to { options.rubixLeft1 },
        SlotType.RUBIX_LEFT_2 to { options.rubixLeft2 },
        SlotType.ALIGN_TARGET to { options.alignTarget },
        SlotType.ALIGN_ACTIVE_CURRENT to { options.alignActiveCurrent },
        SlotType.ALIGN_ACTIVE_OTHER to { options.alignActiveOther },
        SlotType.ALIGN_INACTIVE to { options.alignInactive },
        SlotType.ALIGN_ACTIVE_BUTTON to { options.alignActiveButton },
        SlotType.ALIGN_INACTIVE_BUTTON to { options.alignInactiveButton },
    )

    private var buttons: List<Button<Pair<Int, String?>>>? = null

    private var buttonNonQueue: TerminalButton<Boolean>? = null

    private var buttonReload: TerminalButton<Int>? = null

    private var fade = object : TerminalFade<String>("") {
        override fun renderSingle(
            widgets: MutableList<Widget<*>>,
            tr: Transformation,
            info: String,
            progress: Double,
            isLast: Boolean,
        ) {
            widgets.add(
                TextWidget(
                    info,
                    tr pos Vec2D(0.0, 0.0),
                    Colors.GRAY[3].rgb,
                    tr size 8.0,
                    fontMedium,
                    Vec2D(0.5, 0.0),
                ).alphaScale(progress),
            )
        }
    }

    private val smoothGUI: Boolean
        get() = when (module.data?.terminal) {
            is TerminalOrder -> options.orderSmoothGUI
            is TerminalPanes -> options.panesSmoothGUI
            is TerminalStart -> options.startSmoothGUI
            is TerminalColor -> options.colorSmoothGUI
            is TerminalRubix -> options.rubixSmoothGUI
            is TerminalAlign -> options.alignSmoothGUI
            else -> true
        }

    override val width = 176.0

    override val height get() = 113.0 + 18.0 * (module.data?.terminal?.chestLines ?: 0)

    override val scaleOverride get() = options.scaleOverride.nullableScale

    override fun getFadeOutOrigin(tr: Transformation): Vec2D {
        val lines = module.data?.terminal?.lines ?: 0
        val beginLine = module.data?.terminal?.beginLine ?: 0
        return tr pos Vec2D(88.0, beginLine * 18.0 + 10.0 + 9.0 * (lines - beginLine))
    }

    override val ensureShow get() = BetterTerminal.Screen.proxiedScreen sameNotNull MC.currentScreen

    override fun reset() {
        buttons = null
        buttonNonQueue = null
        buttonReload = null
        fade.reset()
    }

    private val dragClick get() = options.dragClick && module.data?.enableQueue == true

    private var holdingMouseButton: Int? = null

    override fun draw(
        widgets: MutableList<Widget<*>>,
        box: Vec2D,
        tr: Transformation,
    ) {
        val data = module.data ?: return

        val ttr = tr + Vec2D(0.0, data.terminal.beginLine * 18.0)

        options.background.addTo(
            widgets,
            ttr + Vec2D(-18.0, -10.0),
            Vec2D(
                212.0,
                40.0 + 18.0 * (data.terminal.lines - data.terminal.beginLine),
            ),
        )

        val buttonNonQueue = buttonNonQueue ?: object : TerminalButton<Boolean>(false) {
            override fun getColor(hovered: Boolean) = if (hovered || info) Colors.RED[6].rgb else Colors.NONE.rgb

            override val text get() = "NQ"

            override fun onMouseDown(button: Int) {
                BetterTerminal.forceNonQueue()
            }
        }

        val buttonReload = buttonReload ?: object : TerminalButton<Int>(0) {
            override fun getColor(hovered: Boolean) = if (hovered) Colors.YELLOW[6].rgb else Colors.NONE.rgb

            override val text get() = if (info > 0) "$info" else "RS"

            override fun onMouseDown(button: Int) {
                BetterTerminal.reloadTerminal()
            }
        }

        this.buttonNonQueue = buttonNonQueue
        this.buttonReload = buttonReload

        val title = when {
            buttonNonQueue.isHovered(ttr + Vec2D(-10.0, -2.0)) -> {
                if (data.enableQueue) {
                    "[Yqloss] Switch to non-queue mode."
                } else {
                    "[Yqloss] Already in non-queue mode."
                }
            }

            buttonReload.isHovered(ttr + Vec2D(170.0, -2.0)) -> "[Yqloss] Reload terminal state."

            else -> "[Yqloss] ${data.terminal.title}"
        }

        fade.switch(title)
        fade.render(widgets, ttr + Vec2D(88.0, 2.0))

        val buttonCount = data.terminal.lines * 9

        val buttons = buttons?.takeIf { it.size == buttonCount } ?: List(buttonCount) {
            object : TerminalButton<Pair<Int, String?>>(0 to null) {
                override fun getColor(hovered: Boolean) = info.first

                override val text get() = info.second

                private var lastHovered: Int? = null

                override fun onMouseDown(button: Int) {
                    if (!dragClick) {
                        BetterTerminal.onClick(it, button)
                    }
                }

                override fun render(
                    widgets: MutableList<Widget<*>>,
                    tr: Transformation,
                ) {
                    if (dragClick) {
                        val button = holdingMouseButton
                        if (button === null) {
                            lastHovered = null
                        } else {
                            val hovered = isHovered(tr).ifTake { button }
                            if (hovered !== null && hovered != lastHovered) {
                                BetterTerminal.onClick(it, hovered)
                            }
                            lastHovered = hovered
                        }
                    }
                    super.render(widgets, tr)
                }
            }
        }

        this.buttons = buttons

        if (dragClick) {
            holdingMouseButton = when {
                Mouse.isButtonDown(1) -> 1
                Mouse.isButtonDown(0) -> 0
                else -> null
            }
        }

        data.terminal
            .draw(data.state)
            .drop(data.terminal.beginLine * 9)
            .forEachIndexed { slotID, slot ->
                val x = slotID % 9
                val y = slotID / 9
                val slotColor = colorGetterMap[slot.type]?.invoke()?.rgb ?: return@forEachIndexed
                buttons[slotID + data.terminal.beginLine * 9].run {
                    info = slotColor to slot.text
                    render(widgets, ttr + Vec2D(8.0 + 18.0 * x, 18.0 + 18.0 * y))
                }
            }

        buttonNonQueue.info = !data.enableQueue
        buttonReload.info = data.queueSize

        buttonNonQueue.render(widgets, ttr + Vec2D(-10.0, -2.0))
        buttonReload.render(widgets, ttr + Vec2D(170.0, -2.0))
    }

    override val registerEvents: EventRegistry.() -> Unit
        get() = {
            super.registerEvents(this)

            register<YCInputEvent.Mouse.Click> { event ->
                val data = module.data ?: longRet

                event.screen && ensureShow || longRet

                val tr = transformation
                buttons?.withIndex()?.firstOrNull { (i, button) ->
                    val x = i % 9
                    val y = i / 9
                    if (button.isHovered(tr + Vec2D(8.0 + 18.0 * x, 18.0 + 18.0 * y))) {
                        button.onMouseDown(event.button)
                        true
                    } else {
                        false
                    }
                } ?: run {
                    val ttr = tr + Vec2D(0.0, data.terminal.beginLine * 18.0)
                    when {
                        buttonNonQueue?.isHovered(ttr + Vec2D(-10.0, -2.0)) == true -> buttonNonQueue
                        buttonReload?.isHovered(ttr + Vec2D(170.0, -2.0)) == true -> buttonReload
                        else -> null
                    }?.onMouseDown(event.button)
                }
            }
        }

    abstract class TerminalFade<T>(
        initial: T,
    ) : Fade<T>(initial) {
        override val smooth by ::smoothGUI
    }

    abstract class TerminalButton<T>(
        info: T,
    ) : Button<T>(info) {
        override val smooth by ::smoothGUI

        override val cornerRadius get() = options.cornerRadius.double

        override val collisionSize = Vec2D(16.0, 16.0)

        abstract override fun getColor(hovered: Boolean): Int

        abstract val text: String?

        override fun renderIcon(
            widgets: MutableList<Widget<*>>,
            tr: Transformation,
            hovered: Boolean,
        ) {
            val text = text ?: return
            widgets.add(
                TextWidget(
                    text,
                    tr pos Vec2D(0.0, 0.0),
                    Colors.GRAY[3].rgb,
                    tr size 8.0,
                    fontSemiBold,
                    Vec2D(0.5, 0.5),
                ),
            )
        }
    }
}
