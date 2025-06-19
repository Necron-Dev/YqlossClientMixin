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

package yqloss.yqlossclientmixinkt.impl.option

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.SubConfig
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import net.yqloss.uktil.accessor.outs.cast
import net.yqloss.uktil.accessor.outs.inBox
import yqloss.yqlossclientmixinkt.DEV
import yqloss.yqlossclientmixinkt.RT
import yqloss.yqlossclientmixinkt.impl.MOD_VERSION
import yqloss.yqlossclientmixinkt.impl.YCMixin
import yqloss.yqlossclientmixinkt.impl.option.module.*
import yqloss.yqlossclientmixinkt.module.option.YCModuleOptions
import yqloss.yqlossclientmixinkt.ycLogger
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType

private val optionsImplMap = mutableMapOf<KClass<*>, () -> YCModuleOptions>()

private val logger = ycLogger("Config")

object YqlossClientConfig : Config(
    Mod("Yqloss Client $MOD_VERSION ${if (DEV) "DEV " else ""}$RT", ModType.THIRD_PARTY),
    "yqlossclient.json",
) {
    @SubConfig
    var main = MainConfig()

    @SubConfig
    var repository = RepositoryOptionsImpl()

    @SubConfig
    var rawInput = RawInputOptionsImpl()

    @SubConfig
    var ssMotionBlur = SSMotionBlurOptionsImpl()

    @SubConfig
    var tweaks = TweaksOptionsImpl()

    @SubConfig
    var corpseFinder = CorpseFinderOptionsImpl()

    @SubConfig
    var miningPrediction = MiningPredictionOptionsImpl()

    @SubConfig
    var betterTerminal = BetterTerminalOptionsImpl()

    @SubConfig
    var ycLeapMenu = YCLeapMenuOptionsImpl()

    @SubConfig
    var mapMarker = MapMarkerOptionsImpl()

    @SubConfig
    var windowProperties = WindowPropertiesOptionsImpl()

    @SubConfig
    var hotkeys = HotkeysOptionsImpl()

    @SubConfig
    var cursor = CursorOptionsImpl()

    init {
        initialize()

        YqlossClientConfig::class
            .declaredMemberProperties
            .filter { it.returnType.isSubtypeOf(YCModuleOptions::class.starProjectedType) }
            .forEach { property ->
                property(this).let { instance ->
                    logger.info("detected Options implementation $instance")
                    (instance as OptionsImpl).run {
                        initialize()
                        onInitializationPost()
                    }
                    instance::class.allSuperclasses.plus(instance::class).forEach {
                        optionsImplMap[it] = { property(this) as YCModuleOptions }
                    }
                }
            }
    }

    override fun load() {
        super.load()
        val version = ++YCMixin.configVersion
        logger.info("increased config version to $version")
    }

    fun <T : YCModuleOptions> getOptionsImpl(type: KClass<T>): T {
        return optionsImplMap[type]?.invoke()?.inBox?.cast<T>()?.also {
            logger.info("loaded Options implementation $it for type $type")
        } ?: throw NotImplementedError("$type")
    }
}
