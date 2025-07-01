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
        text = "{module.better_terminal.config.header.module}",
        size = 2,
    )
    val headerModule = false

    @Extract
    var scaleOverride = ScreenScaleOption()

    @Switch(
        name = "{module.better_terminal.config.option.enable_queueing_clicks.text}",
        description = "{module.better_terminal.config.option.enable_queueing_clicks.description}",
        size = 1,
    )
    var enableQueueOption = false

    @Switch(
        name = "{module.better_terminal.config.option.reload_state_when_state_mismatch.text}",
        description = "{module.better_terminal.config.option.reload_state_when_state_mismatch.description}",
        size = 1,
    )
    var reloadOnMismatchOption = false

    @Switch(
        name = "{module.better_terminal.config.option.prevent_fail.text}",
        description = "{module.better_terminal.config.option.prevent_fail.description}",
        size = 1,
    )
    var preventFailOption = false

    @Switch(
        name = "{module.better_terminal.config.option.prevent_misclick.text}",
        description = "{module.better_terminal.config.option.prevent_misclick.description}",
        size = 1,
    )
    var preventMisclickOption = false

    @Number(
        name = "{module.better_terminal.config.option.click_delay_from_ticks.text}",
        description = "{module.better_terminal.config.option.click_delay_from_ticks.description}",
        min = 0.0F,
        max = Float.MAX_VALUE,
        size = 1,
    )
    var clickDelayFromOption = 2.0F

    @Number(
        name = "{module.better_terminal.config.option.click_delay_until_ticks.text}",
        description = "{module.better_terminal.config.option.click_delay_until_ticks.description}",
        min = 0.0F,
        max = Float.MAX_VALUE,
        size = 1,
    )
    var clickDelayUntilOption = 4.0F

    @Number(
        name = "{module.better_terminal.config.option.slot_rounded_corner_radius.text}",
        description = "{module.better_terminal.config.option.slot_rounded_corner_radius.description}",
        min = 0.0F,
        max = Float.MAX_VALUE,
        size = 1,
    )
    var cornerRadius = 4.0F

    @Switch(
        name = "{module.better_terminal.config.option.show_vanilla_chest_gui.text}",
        description = "{module.better_terminal.config.option.show_vanilla_chest_gui.description}",
        size = 1,
    )
    var showChestOption = false

    @Slider(
        name = "{module.better_terminal.config.option.vanilla_chest_gui_scale.text}",
        description = "{module.better_terminal.config.option.vanilla_chest_gui_scale.description}",
        min = 0.0F,
        max = 1.0F,
    )
    var chestScaleOption = 0.25F

    @Switch(
        name = "{module.better_terminal.config.option.drag_click.text}",
        description = "{module.better_terminal.config.option.drag_click.description}",
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
        text = "{module.better_terminal.config.header.order}",
        size = 2,
    )
    val headerOrder = false

    @Switch(
        name = "{module.better_terminal.config.option.order_enabled.text}",
        description = "{module.better_terminal.config.option.order_enabled.description}",
        size = 1,
    )
    var orderEnabledOption = true

    @Switch(
        name = "{module.better_terminal.config.option.order_smooth_gui.text}",
        description = "{module.better_terminal.config.option.order_smooth_gui.description}",
        size = 1,
    )
    var orderSmoothGUI = true

    @Switch(
        name = "{module.better_terminal.config.option.order_show_number.text}",
        description = "{module.better_terminal.config.option.order_show_number.description}",
        size = 1,
    )
    var orderShowNumberOption = true

    @Switch(
        name = "{module.better_terminal.config.option.order_show_finished_number.text}",
        description = "{module.better_terminal.config.option.order_show_finished_number.description}",
        size = 1,
    )
    var orderShowClickedNumberOption = false

    @Color(
        name = "{module.better_terminal.config.option.order_first_color.text}",
        description = "{module.better_terminal.config.option.order_first_color.description}",
        size = 1,
    )
    var order1 = Colors.GREEN[6]

    @Color(
        name = "{module.better_terminal.config.option.order_second_color.text}",
        description = "{module.better_terminal.config.option.order_second_color.description}",
        size = 1,
    )
    var order2 = Colors.YELLOW[6]

    @Color(
        name = "{module.better_terminal.config.option.order_third_color.text}",
        description = "{module.better_terminal.config.option.order_third_color.description}",
        size = 1,
    )
    var order3 = Colors.RED[6]

    @Color(
        name = "{module.better_terminal.config.option.order_finished_color.text}",
        description = "{module.better_terminal.config.option.order_finished_color.description}",
        size = 1,
    )
    var orderClicked = Colors.NONE

    @Color(
        name = "{module.better_terminal.config.option.order_other_color.text}",
        description = "{module.better_terminal.config.option.order_other_color.description}",
        size = 1,
    )
    var orderOther = Colors.GRAY[8]

    @Transient
    @Header(
        text = "{module.better_terminal.config.header.panes}",
        size = 2,
    )
    val headerPanes = false

    @Switch(
        name = "{module.better_terminal.config.option.panes_enabled.text}",
        description = "{module.better_terminal.config.option.panes_enabled.description}",
        size = 1,
    )
    var panesEnabledOption = true

    @Switch(
        name = "{module.better_terminal.config.option.panes_smooth_gui.text}",
        description = "{module.better_terminal.config.option.panes_smooth_gui.description}",
        size = 1,
    )
    var panesSmoothGUI = true

    @Color(
        name = "{module.better_terminal.config.option.panes_on_color.text}",
        description = "{module.better_terminal.config.option.panes_on_color.description}",
        size = 1,
    )
    var panesOn = Colors.GREEN[6]

    @Color(
        name = "{module.better_terminal.config.option.panes_off_color.text}",
        description = "{module.better_terminal.config.option.panes_off_color.description}",
        size = 1,
    )
    var panesOff = Colors.RED[6]

    @Transient
    @Header(
        text = "{module.better_terminal.config.header.start}",
        size = 2,
    )
    val headerStart = false

    @Switch(
        name = "{module.better_terminal.config.option.start_enabled.text}",
        description = "{module.better_terminal.config.option.start_enabled.description}",
        size = 1,
    )
    var startEnabledOption = true

    @Switch(
        name = "{module.better_terminal.config.option.start_smooth_gui.text}",
        description = "{module.better_terminal.config.option.start_smooth_gui.description}",
        size = 1,
    )
    var startSmoothGUI = true

    @Color(
        name = "{module.better_terminal.config.option.start_answer_color.text}",
        description = "{module.better_terminal.config.option.start_answer_color.description}",
        size = 1,
    )
    var startAnswer = Colors.GREEN[6]

    @Color(
        name = "{module.better_terminal.config.option.start_clicked_answer_color.text}",
        description = "{module.better_terminal.config.option.start_clicked_answer_color.description}",
        size = 1,
    )
    var startClicked = Colors.GRAY[8]

    @Color(
        name = "{module.better_terminal.config.option.start_other_color.text}",
        description = "{module.better_terminal.config.option.start_other_color.description}",
        size = 1,
    )
    var startOther = Colors.NONE

    @Transient
    @Header(
        text = "{module.better_terminal.config.header.color}",
        size = 2,
    )
    val headerColor = false

    @Switch(
        name = "{module.better_terminal.config.option.color_enabled.text}",
        description = "{module.better_terminal.config.option.color_enabled.description}",
        size = 1,
    )
    var colorEnabledOption = true

    @Switch(
        name = "{module.better_terminal.config.option.color_smooth_gui.text}",
        description = "{module.better_terminal.config.option.color_smooth_gui.description}",
        size = 1,
    )
    var colorSmoothGUI = true

    @Color(
        name = "{module.better_terminal.config.option.color_answer_color.text}",
        description = "{module.better_terminal.config.option.color_answer_color.description}",
        size = 1,
    )
    var colorAnswer = Colors.GREEN[6]

    @Color(
        name = "{module.better_terminal.config.option.color_clicked_answer_color.text}",
        description = "{module.better_terminal.config.option.color_clicked_answer_color.description}",
        size = 1,
    )
    var colorClicked = Colors.GRAY[8]

    @Color(
        name = "{module.better_terminal.config.option.color_other_color.text}",
        description = "{module.better_terminal.config.option.color_other_color.description}",
        size = 1,
    )
    var colorOther = Colors.NONE

    @Transient
    @Header(
        text = "{module.better_terminal.config.header.rubix}",
        size = 2,
    )
    val headerRubix = false

    @Switch(
        name = "{module.better_terminal.config.option.rubix_enabled.text}",
        description = "{module.better_terminal.config.option.rubix_enabled.description}",
        size = 1,
    )
    var rubixEnabledOption = true

    @Switch(
        name = "{module.better_terminal.config.option.rubix_smooth_gui.text}",
        description = "{module.better_terminal.config.option.rubix_smooth_gui.description}",
        size = 1,
    )
    var rubixSmoothGUI = true

    @Switch(
        name = "{module.better_terminal.config.option.rubix_show_number.text}",
        description = "{module.better_terminal.config.option.rubix_show_number.description}",
        size = 1,
    )
    var rubixShowNumberOption = true

    @Color(
        name = "{module.better_terminal.config.option.rubix_color_0.text}",
        description = "{module.better_terminal.config.option.rubix_color_0.description}",
        size = 1,
    )
    var rubix0 = Colors.GRAY[8]

    @Color(
        name = "{module.better_terminal.config.option.rubix_color_minus_1.text}",
        description = "{module.better_terminal.config.option.rubix_color_minus_1.description}",
        size = 1,
    )
    var rubixRight1 = Colors.TEAL[8]

    @Color(
        name = "{module.better_terminal.config.option.rubix_color_minus_2.text}",
        description = "{module.better_terminal.config.option.rubix_color_minus_2.description}",
        size = 1,
    )
    var rubixRight2 = Colors.TEAL[5]

    @Color(
        name = "{module.better_terminal.config.option.rubix_color_1.text}",
        description = "{module.better_terminal.config.option.rubix_color_1.description}",
        size = 1,
    )
    var rubixLeft1 = Colors.INDIGO[8]

    @Color(
        name = "{module.better_terminal.config.option.rubix_color_2.text}",
        description = "{module.better_terminal.config.option.rubix_color_2.description}",
        size = 1,
    )
    var rubixLeft2 = Colors.INDIGO[5]

    @Switch(
        name = "{module.better_terminal.config.option.rubix_correct_direction.text}",
        description = "{module.better_terminal.config.option.rubix_correct_direction.description}",
        size = 1,
    )
    var rubixCorrectDirectionOption = false

    @Transient
    @Header(
        text = "{module.better_terminal.config.header.align}",
        size = 2,
    )
    val headerAlign = false

    @Switch(
        name = "{module.better_terminal.config.option.align_enabled.text}",
        description = "{module.better_terminal.config.option.align_enabled.description}",
        size = 1,
    )
    var alignEnabledOption = true

    @Switch(
        name = "{module.better_terminal.config.option.align_smooth_gui.text}",
        description = "{module.better_terminal.config.option.align_smooth_gui.description}",
        size = 1,
    )
    var alignSmoothGUI = true

    @Color(
        name = "{module.better_terminal.config.option.align_target_color.text}",
        description = "{module.better_terminal.config.option.align_target_color.description}",
        size = 1,
    )
    var alignTarget = Colors.GRAPE[6]

    @Color(
        name = "{module.better_terminal.config.option.align_inactive_color.text}",
        description = "{module.better_terminal.config.option.align_inactive_color.description}",
        size = 1,
    )
    var alignInactive = Colors.GRAY[8]

    @Color(
        name = "{module.better_terminal.config.option.align_current_active_color.text}",
        description = "{module.better_terminal.config.option.align_current_active_color.description}",
        size = 1,
    )
    var alignActiveCurrent = Colors.GREEN[6]

    @Color(
        name = "{module.better_terminal.config.option.align_other_active_color.text}",
        description = "{module.better_terminal.config.option.align_other_active_color.description}",
        size = 1,
    )
    var alignActiveOther = Colors.RED[6]

    @Color(
        name = "{module.better_terminal.config.option.align_lock_in_slot_color.text}",
        description = "{module.better_terminal.config.option.align_lock_in_slot_color.description}",
        size = 1,
    )
    var alignActiveButton = Colors.GREEN[6]

    @Color(
        name = "{module.better_terminal.config.option.align_row_not_active_color.text}",
        description = "{module.better_terminal.config.option.align_row_not_active_color.description}",
        size = 1,
    )
    var alignInactiveButton = Colors.GRAY[8]

    @Transient
    @Header(
        text = "{module.better_terminal.config.header.notification_correct_click}",
        size = 2,
    )
    val headerCorrect = false

    @Extract
    var onCorrectClickOption = NotificationOption()

    @Transient
    @Header(
        text = "{module.better_terminal.config.header.notification_canceled_click}",
        size = 2,
    )
    val headerCanceled = false

    @Extract
    var onCanceledClickOption = NotificationOption()

    @Transient
    @Header(
        text = "{module.better_terminal.config.header.notification_wrong_click}",
        size = 2,
    )
    val headerWrong = false

    @Extract
    var onWrongClickOption = NotificationOption()

    @Transient
    @Header(
        text = "{module.better_terminal.config.header.notification_fail_click}",
        size = 2,
    )
    val headerFail = false

    @Extract
    var onFailClickOption = NotificationOption()

    @Transient
    @Header(
        text = "{module.better_terminal.config.header.notification_non_queued_click}",
        size = 2,
    )
    val headerNonQueued = false

    @Extract
    var onNonQueuedClickOption = NotificationOption()

    @Transient
    @Header(
        text = "{module.better_terminal.config.header.notification_actual_click_sent_to_server}",
        size = 2,
    )
    val headerActual = false

    @Extract
    var onActualClickOption = NotificationOption()

    @Transient
    @Header(
        text = "{module.better_terminal.config.header.debug}",
        size = 2,
    )
    val headerDebug = false

    @Switch(
        name = "{module.better_terminal.config.option.force_enabled.text}",
        description = "{module.better_terminal.config.option.force_enabled.description}",
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

    override fun onInitializationPost() {
        requirePlus(
            "unknownMacro",
            "enableQueueOption",
            "reloadOnMismatchOption",
            "clickDelayFromOption",
            "clickDelayUntilOption",
            "dragClick",
            "headerCorrect",
            "onCorrectClickOption",
            "headerWrong",
            "onWrongClickOption",
            "headerFail",
            "onFailClickOption",
        )
    }
}
