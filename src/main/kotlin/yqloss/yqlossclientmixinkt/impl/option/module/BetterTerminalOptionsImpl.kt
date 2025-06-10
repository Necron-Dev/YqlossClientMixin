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

package yqloss.yqlossclientmixinkt.impl.option.module

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.annotations.Number
import net.yqloss.uktil.extension.double
import yqloss.yqlossclientmixinkt.impl.option.OptionsImpl
import yqloss.yqlossclientmixinkt.impl.option.adapter.Extract
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerAtOwnRisk
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerRequireHypixelModAPI
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerUnknownMacro
import yqloss.yqlossclientmixinkt.impl.option.gui.GUIBackground
import yqloss.yqlossclientmixinkt.impl.option.impl.NotificationOption
import yqloss.yqlossclientmixinkt.impl.option.impl.ScreenScaleOption
import yqloss.yqlossclientmixinkt.impl.util.Colors
import yqloss.yqlossclientmixinkt.module.betterterminal.BetterTerminalOptions
import yqloss.yqlossclientmixinkt.module.betterterminal.INFO_BETTER_TERMINAL

class BetterTerminalOptionsImpl :
    OptionsImpl(INFO_BETTER_TERMINAL),
    BetterTerminalOptions {
    @Transient
    @Extract
    val disclaimer = DisclaimerAtOwnRisk()

    @Transient
    @Extract
    val unknownMacro = DisclaimerUnknownMacro()

    @Transient
    @Extract
    val requireHypixelModAPI = DisclaimerRequireHypixelModAPI()

    @Transient
    @Header(
        text = "Better Terminal",
        size = 2,
    )
    val headerModule = false

    @Extract
    var scaleOverride = ScreenScaleOption()

    @Switch(
        name = "Enable Queueing Clicks",
        size = 1,
    )
    var enableQueueOption = false

    @Switch(
        name = "Reload State When State Mismatch",
        size = 1,
    )
    var reloadOnMismatchOption = false

    @Switch(
        name = "Prevent Fail",
        size = 1,
    )
    var preventFailOption = false

    @Switch(
        name = "Prevent Misclick",
        size = 1,
    )
    var preventMisclickOption = false

    @Number(
        name = "Click Delay From (in ticks)",
        min = 0.0F,
        max = Float.MAX_VALUE,
        size = 1,
    )
    var clickDelayFromOption = 2.0F

    @Number(
        name = "Click Delay Until (in ticks)",
        min = 0.0F,
        max = Float.MAX_VALUE,
        size = 1,
    )
    var clickDelayUntilOption = 4.0F

    @Number(
        name = "Slot Rounded Corner Radius",
        min = 0.0F,
        max = Float.MAX_VALUE,
        size = 1,
    )
    var cornerRadius = 4.0F

    @Switch(
        name = "Show Vanilla Chest GUI",
        size = 1,
    )
    var showChestOption = false

    @Slider(
        name = "Vanilla Chest GUI Scale",
        min = 0.0F,
        max = 1.0F,
    )
    var chestScaleOption = 0.25F

    @Switch(
        name = "Drag Click",
        size = 2,
    )
    var dragClick = false

    @Extract
    var background =
        GUIBackground().apply {
            radiusOption = 12.0F
            paddingXOption = 0.0F
            paddingYOption = 0.0F
        }

    @Transient
    @Header(
        text = "Click in order!",
        size = 2,
    )
    val headerOrder = false

    @Switch(
        name = "Enabled",
        size = 1,
    )
    var orderEnabledOption = true

    @Switch(
        name = "Smooth GUI",
        size = 1,
    )
    var orderSmoothGUI = true

    @Switch(
        name = "Show Number",
        size = 1,
    )
    var orderShowNumberOption = true

    @Switch(
        name = "Show Finished Number",
        size = 1,
    )
    var orderShowClickedNumberOption = false

    @Color(
        name = "First",
        size = 1,
    )
    var order1 = Colors.GREEN[6]

    @Color(
        name = "Second",
        size = 1,
    )
    var order2 = Colors.YELLOW[6]

    @Color(
        name = "Third",
        size = 1,
    )
    var order3 = Colors.RED[6]

    @Color(
        name = "Finished",
        size = 1,
    )
    var orderClicked = Colors.NONE

    @Color(
        name = "Other",
        size = 1,
    )
    var orderOther = Colors.GRAY[8]

    @Transient
    @Header(
        text = "Correct all the panes!",
        size = 2,
    )
    val headerPanes = false

    @Switch(
        name = "Enabled",
        size = 1,
    )
    var panesEnabledOption = true

    @Switch(
        name = "Smooth GUI",
        size = 1,
    )
    var panesSmoothGUI = true

    @Color(
        name = "ON",
        size = 1,
    )
    var panesOn = Colors.GREEN[6]

    @Color(
        name = "OFF",
        size = 1,
    )
    var panesOff = Colors.RED[6]

    @Transient
    @Header(
        text = "What starts with: '?'?",
        size = 2,
    )
    val headerStart = false

    @Switch(
        name = "Enabled",
        size = 1,
    )
    var startEnabledOption = true

    @Switch(
        name = "Smooth GUI",
        size = 1,
    )
    var startSmoothGUI = true

    @Color(
        name = "Answer",
        size = 1,
    )
    var startAnswer = Colors.GREEN[6]

    @Color(
        name = "Clicked Answer",
        size = 1,
    )
    var startClicked = Colors.GRAY[8]

    @Color(
        name = "Other",
        size = 1,
    )
    var startOther = Colors.NONE

    @Transient
    @Header(
        text = "Select all the COLOR items!",
        size = 2,
    )
    val headerColor = false

    @Switch(
        name = "Enabled",
        size = 1,
    )
    var colorEnabledOption = true

    @Switch(
        name = "Smooth GUI",
        size = 1,
    )
    var colorSmoothGUI = true

    @Color(
        name = "Answer",
        size = 1,
    )
    var colorAnswer = Colors.GREEN[6]

    @Color(
        name = "Clicked Answer",
        size = 1,
    )
    var colorClicked = Colors.GRAY[8]

    @Color(
        name = "Other",
        size = 1,
    )
    var colorOther = Colors.NONE

    @Transient
    @Header(
        text = "Change all to same color!",
        size = 2,
    )
    val headerRubix = false

    @Switch(
        name = "Enabled",
        size = 1,
    )
    var rubixEnabledOption = true

    @Switch(
        name = "Smooth GUI",
        size = 1,
    )
    var rubixSmoothGUI = true

    @Switch(
        name = "Show Number",
        size = 1,
    )
    var rubixShowNumberOption = true

    @Color(
        name = "0",
        size = 1,
    )
    var rubix0 = Colors.GRAY[8]

    @Color(
        name = "-1",
        size = 1,
    )
    var rubixRight1 = Colors.TEAL[8]

    @Color(
        name = "-2",
        size = 1,
    )
    var rubixRight2 = Colors.TEAL[5]

    @Color(
        name = "1",
        size = 1,
    )
    var rubixLeft1 = Colors.INDIGO[8]

    @Color(
        name = "2",
        size = 1,
    )
    var rubixLeft2 = Colors.INDIGO[5]

    @Switch(
        name = "Correct Direction",
        size = 1,
    )
    var rubixCorrectDirectionOption = false

    @Transient
    @Header(
        text = "Click the button on time!",
        size = 2,
    )
    val headerAlign = false

    @Switch(
        name = "Enabled",
        size = 1,
    )
    var alignEnabledOption = true

    @Switch(
        name = "Smooth GUI",
        size = 1,
    )
    var alignSmoothGUI = true

    @Color(
        name = "Target",
        size = 1,
    )
    var alignTarget = Colors.GRAPE[6]

    @Color(
        name = "Inactive",
        size = 1,
    )
    var alignInactive = Colors.GRAY[8]

    @Color(
        name = "Current Active",
        size = 1,
    )
    var alignActiveCurrent = Colors.GREEN[6]

    @Color(
        name = "Other Active",
        size = 1,
    )
    var alignActiveOther = Colors.RED[6]

    @Color(
        name = "Lock In Slot",
        size = 1,
    )
    var alignActiveButton = Colors.GREEN[6]

    @Color(
        name = "Row Not Active",
        size = 1,
    )
    var alignInactiveButton = Colors.GRAY[8]

    @Transient
    @Header(
        text = "Notification On Correct Click",
        size = 2,
    )
    val headerCorrect = false

    @Extract
    var onCorrectClickOption = NotificationOption()

    @Transient
    @Header(
        text = "Notification On Canceled Click",
        size = 2,
    )
    val headerCanceled = false

    @Extract
    var onCanceledClickOption = NotificationOption()

    @Transient
    @Header(
        text = "Notification On Wrong Click",
        size = 2,
    )
    val headerWrong = false

    @Extract
    var onWrongClickOption = NotificationOption()

    @Transient
    @Header(
        text = "Notification On Fail Click",
        size = 2,
    )
    val headerFail = false

    @Extract
    var onFailClickOption = NotificationOption()

    @Transient
    @Header(
        text = "Notification On Non Queued Click",
        size = 2,
    )
    val headerNonQueued = false

    @Extract
    var onNonQueuedClickOption = NotificationOption()

    @Transient
    @Header(
        text = "Notification On Actual Click (Sent To Server)",
        size = 2,
    )
    val headerActual = false

    @Extract
    var onActualClickOption = NotificationOption()

    @Transient
    @Header(
        text = "Debug",
        size = 2,
    )
    val headerDebug = false

    @Switch(
        name = "Force Enabled",
        size = 1,
    )
    var forceEnabledOption = false

    override val enableQueue by ::enableQueueOption
    override val preventFail by ::preventFailOption
    override val preventMisclick by ::preventMisclickOption
    override val reloadOnMismatch by ::reloadOnMismatchOption
    override val clickDelayFrom get() = clickDelayFromOption.double
    override val clickDelayUntil get() = clickDelayUntilOption.double
    override val orderEnabled by ::orderEnabledOption
    override val orderShowNumber by ::orderShowNumberOption
    override val orderShowClickedNumber by ::orderShowClickedNumberOption
    override val panesEnabled by ::panesEnabledOption
    override val startEnabled by ::startEnabledOption
    override val colorEnabled by ::colorEnabledOption
    override val rubixEnabled by ::rubixEnabledOption
    override val rubixShowNumber by ::rubixShowNumberOption
    override val rubixCorrectDirection by ::rubixCorrectDirectionOption
    override val alignEnabled by ::alignEnabledOption
    override val onCorrectClick by ::onCorrectClickOption
    override val onCanceledClick by ::onCanceledClickOption
    override val onWrongClick by ::onWrongClickOption
    override val onFailClick by ::onFailClickOption
    override val onNonQueuedClick by ::onNonQueuedClickOption
    override val onActualClick by ::onActualClickOption
    override val showChest by ::showChestOption
    override val chestScale get() = chestScaleOption.double
    override val forceEnabled by ::forceEnabledOption
}
