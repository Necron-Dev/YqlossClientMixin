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
import cc.polyfrost.oneconfig.config.data.InfoType
import yqloss.yqlossclientmixinkt.impl.option.OptionsImpl
import yqloss.yqlossclientmixinkt.impl.option.YCHUD
import yqloss.yqlossclientmixinkt.impl.option.adapter.Extract
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerAtOwnRisk
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerLegit
import yqloss.yqlossclientmixinkt.impl.option.disclaimer.DisclaimerRequireHypixelModAPI
import yqloss.yqlossclientmixinkt.impl.option.gui.GUIBackground
import yqloss.yqlossclientmixinkt.impl.option.impl.BlockOption
import yqloss.yqlossclientmixinkt.impl.option.impl.NotificationOption
import yqloss.yqlossclientmixinkt.impl.util.Colors
import yqloss.yqlossclientmixinkt.module.miningprediction.INFO_MINING_PREDICTION
import yqloss.yqlossclientmixinkt.module.miningprediction.MiningPrediction
import yqloss.yqlossclientmixinkt.module.miningprediction.MiningPredictionOptions

class MiningPredictionOptionsImpl :
    OptionsImpl(INFO_MINING_PREDICTION),
    MiningPredictionOptions {
    @Transient
    @Extract
    val disclaimer = DisclaimerAtOwnRisk()

    @Transient
    @Extract
    val legit = DisclaimerLegit()

    @Transient
    @Extract
    val requireHypixelModAPI = DisclaimerRequireHypixelModAPI()

    @Transient
    @Header(
        text = "{module.mining_prediction.config.header.module}",
        size = 2,
    )
    val headerModule = false

    @Transient
    @Info(
        text =
        "{module.mining_prediction.config.info.warning_usage}",
        type = InfoType.WARNING,
        size = 2,
    )
    val warningUsage = false

    @Transient
    @Info(
        text = "{module.mining_prediction.config.info.warning_islands}",
        type = InfoType.WARNING,
        size = 2,
    )
    val warningIslands = false

    @Transient
    @Info(
        text = "{module.mining_prediction.config.info.warning_widget}",
        type = InfoType.WARNING,
        size = 2,
    )
    val warningWidget = false

    @Transient
    @Info(
        text = "{module.mining_prediction.config.info.warning_override}",
        type = InfoType.WARNING,
        size = 2,
    )
    val warningOverride = false

    @Number(
        name = "{module.mining_prediction.config.option.breaking_time_offset.text}",
        description = "{module.mining_prediction.config.option.breaking_time_offset.description}",
        min = -Float.MAX_VALUE,
        max = Float.MAX_VALUE,
        step = 1,
        size = 1,
    )
    var offsetOption = 0

    @Number(
        name = "{module.mining_prediction.config.option.gemstone_mining_speed_offset.text}",
        description = "{module.mining_prediction.config.option.gemstone_mining_speed_offset.description}",
        min = -Float.MAX_VALUE,
        max = Float.MAX_VALUE,
        step = 1,
        size = 1,
    )
    var gemstoneMiningSpeedOffsetOption = 0

    @Number(
        name = "{module.mining_prediction.config.option.dwarven_metal_mining_speed_offset.text}",
        description = "{module.mining_prediction.config.option.dwarven_metal_mining_speed_offset.description}",
        min = -Float.MAX_VALUE,
        max = Float.MAX_VALUE,
        step = 1,
        size = 1,
    )
    var dwarvenMetalMiningSpeedOffsetOption = 0

    @Number(
        name = "{module.mining_prediction.config.option.general_mining_speed_offset.text}",
        description = "{module.mining_prediction.config.option.general_mining_speed_offset.description}",
        min = -Float.MAX_VALUE,
        max = Float.MAX_VALUE,
        step = 1,
        size = 1,
    )
    var generalMiningSpeedOffsetOption = 0

    @Switch(
        name = "{module.mining_prediction.config.option.enable_gemstone_mining_speed_override.text}",
        description = "{module.mining_prediction.config.option.enable_gemstone_mining_speed_override.description}",
        size = 1,
    )
    var enableGemstoneMiningSpeedOverrideOption = false

    @Number(
        name = "{module.mining_prediction.config.option.gemstone_mining_speed_override.text}",
        description = "{module.mining_prediction.config.option.gemstone_mining_speed_override.description}",
        min = -Float.MAX_VALUE,
        max = Float.MAX_VALUE,
        step = 1,
        size = 1,
    )
    var gemstoneMiningSpeedOverrideOption = 0

    @Switch(
        name = "{module.mining_prediction.config.option.enable_dwarven_metal_mining_speed_override.text}",
        description = "{module.mining_prediction.config.option.enable_dwarven_metal_mining_speed_override.description}",
        size = 1,
    )
    var enableDwarvenMetalMiningSpeedOverrideOption = false

    @Number(
        name = "{module.mining_prediction.config.option.dwarven_metal_mining_speed_override.text}",
        description = "{module.mining_prediction.config.option.dwarven_metal_mining_speed_override.description}",
        min = -Float.MAX_VALUE,
        max = Float.MAX_VALUE,
        step = 1,
        size = 1,
    )
    var dwarvenMetalMiningSpeedOverrideOption = 0

    @Switch(
        name = "{module.mining_prediction.config.option.enable_general_mining_speed_override.text}",
        description = "{module.mining_prediction.config.option.enable_general_mining_speed_override.description}",
        size = 1,
    )
    var enableGeneralMiningSpeedOverrideOption = false

    @Number(
        name = "{module.mining_prediction.config.option.general_mining_speed_override.text}",
        description = "{module.mining_prediction.config.option.general_mining_speed_override.description}",
        min = -Float.MAX_VALUE,
        max = Float.MAX_VALUE,
        step = 1,
        size = 1,
    )
    var generalMiningSpeedOverrideOption = 0

    @Transient
    @Header(
        text = "{module.mining_prediction.config.header.replacement_block}",
        size = 2,
    )
    val headerReplacement = false

    @Extract
    var destroyedBlockOption = BlockOption()

    @Transient
    @Header(
        text = "{module.mining_prediction.config.header.break_block_notification}",
        size = 2,
    )
    val headerBreakBlockNotification = false

    @Extract
    var onBreakBlockOption = NotificationOption()

    @HUD(
        name = "Progress HUD",
    )
    var hud = YCHUD()

    @Extract
    var background = GUIBackground()

    @Number(
        name = "{module.mining_prediction.config.option.width.text}",
        description = "{module.mining_prediction.config.option.width.description}",
        min = 1.0F,
        max = Float.MAX_VALUE,
        size = 1,
    )
    var width = 80.0F

    @Number(
        name = "{module.mining_prediction.config.option.progress_bar_height.text}",
        description = "{module.mining_prediction.config.option.progress_bar_height.description}",
        min = 1.0F,
        max = Float.MAX_VALUE,
        size = 1,
    )
    var progressHeight = 4.0F

    @Color(
        name = "{module.mining_prediction.config.option.progress_bar_color_digging.text}",
        description = "{module.mining_prediction.config.option.progress_bar_color_digging.description}",
        size = 1,
    )
    var progressForeground = Colors.GREEN[6]

    @Color(
        name = "{module.mining_prediction.config.option.progress_bar_color_destroyed.text}",
        description = "{module.mining_prediction.config.option.progress_bar_color_destroyed.description}",
        size = 1,
    )
    var progressForegroundOnBreak = Colors.YELLOW[6]

    @Color(
        name = "{module.mining_prediction.config.option.progress_bar_background_color.text}",
        description = "{module.mining_prediction.config.option.progress_bar_background_color.description}",
        size = 1,
    )
    var progressBackground = Colors.GRAY[3]

    @Number(
        name = "{module.mining_prediction.config.option.font_size.text}",
        description = "{module.mining_prediction.config.option.font_size.description}",
        min = 1.0F,
        max = Float.MAX_VALUE,
        size = 1,
    )
    var textSize = 8.0F

    @Color(
        name = "{module.mining_prediction.config.option.left_text_color.text}",
        description = "{module.mining_prediction.config.option.left_text_color.description}",
        size = 1,
    )
    var textColorLeft = Colors.GRAY[3]

    @Color(
        name = "{module.mining_prediction.config.option.right_text_color.text}",
        description = "{module.mining_prediction.config.option.right_text_color.description}",
        size = 1,
    )
    var textColorRight = Colors.GRAY[3]

    @Text(
        name = "{module.mining_prediction.config.option.left_text.text}",
        description = "{module.mining_prediction.config.option.left_text.description}",
        size = 1,
    )
    var textLeft = "<ore.displayName>"

    @Text(
        name = "{module.mining_prediction.config.option.right_text.text}",
        description = "{module.mining_prediction.config.option.right_text.description}",
        size = 1,
    )
    var textRight = "<normalizedProgress> / <ticks>"

    @Number(
        name = "{module.mining_prediction.config.option.text_progress_gap.text}",
        description = "{module.mining_prediction.config.option.text_progress_gap.description}",
        min = 1.0F,
        max = Float.MAX_VALUE,
        size = 1,
    )
    var textProgressGap = 4.0F

    @Transient
    @Header(
        text = "{module.mining_prediction.config.header.debug}",
        size = 2,
    )
    val headerDebug = false

    @Switch(
        name = "{module.mining_prediction.config.option.force_enabled.text}",
        description = "{module.mining_prediction.config.option.force_enabled.description}",
        size = 1,
    )
    var forceEnabledOption = false

    @Switch(
        name = "{module.mining_prediction.config.option.force_example.text}",
        description = "{module.mining_prediction.config.option.force_example.description}",
        size = 1,
    )
    var forceExample = false

    @Switch(
        name = "{module.mining_prediction.config.option.use_client_tick.text}",
        description = "{module.mining_prediction.config.option.use_client_tick.description}",
        size = 1,
    )
    var useClientTickOption = false

    @Transient
    @Extract
    val printDebugInfo =
        @Button(
            name = "{module.mining_prediction.config.function.print_debug_info.text}",
            text = "{module.mining_prediction.config.function.print_debug_info.button}",
            description = "{module.mining_prediction.config.function.print_debug_info.description}",
            size = 1,
        )
        {
            MiningPrediction.printDebugInfo()
        }

    override val onBreakBlock by ::onBreakBlockOption
    override val durationOffset by ::offsetOption
    override val destroyedBlock by ::destroyedBlockOption
    override val generalMiningSpeedOffset by ::generalMiningSpeedOffsetOption
    override val gemstoneMiningSpeedOffset by ::gemstoneMiningSpeedOffsetOption
    override val dwarvenMetalMiningSpeedOffset by ::dwarvenMetalMiningSpeedOffsetOption
    override val enableGeneralMiningSpeedOverride by ::enableGeneralMiningSpeedOverrideOption
    override val generalMiningSpeedOverride by ::generalMiningSpeedOverrideOption
    override val enableGemstoneMiningSpeedOverride by ::enableGemstoneMiningSpeedOverrideOption
    override val gemstoneMiningSpeedOverride by ::gemstoneMiningSpeedOverrideOption
    override val enableDwarvenMetalMiningSpeedOverride by ::enableDwarvenMetalMiningSpeedOverrideOption
    override val dwarvenMetalMiningSpeedOverride by ::dwarvenMetalMiningSpeedOverrideOption
    override val forceEnabled by ::forceEnabledOption
    override val useClientTick by ::useClientTickOption
}
