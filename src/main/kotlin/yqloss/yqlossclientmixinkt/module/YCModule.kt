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

package yqloss.yqlossclientmixinkt.module

import yqloss.yqlossclientmixinkt.YC
import yqloss.yqlossclientmixinkt.event.OperationEventRegistry
import yqloss.yqlossclientmixinkt.event.YCEventRegistration
import yqloss.yqlossclientmixinkt.event.YCEventRegistration.Entry
import yqloss.yqlossclientmixinkt.event.registerEventEntries
import yqloss.yqlossclientmixinkt.module.option.YCModuleOptions
import yqloss.yqlossclientmixinkt.util.accessor.getValue
import yqloss.yqlossclientmixinkt.util.accessor.refs.trigger

interface YCModule<T : YCModuleOptions> {
    val id: String
    val name: String
    val options: T
}

abstract class YCModuleInfo<T : YCModuleOptions> : YCModule<T> {
    inline val configFile get() = "yqlossclient-$id.json"
}

inline fun <reified T : YCModuleOptions> moduleInfo(
    id: String,
    name: String,
): YCModuleInfo<T> {
    return object : YCModuleInfo<T>() {
        override val id = id
        override val name = name
        override val options by trigger(YC::configVersion) { YC.getOptionsImpl(T::class) }
    }
}

val NO_MODULE_INFO = moduleInfo<YCModuleOptions>("", "")

fun <T> T.buildRegisterEventEntries(
    function: OperationEventRegistry.() -> Unit,
): List<Entry> where T : YCEventRegistration, T : YCModule<*> {
    return mutableListOf<Entry>()
        .also { function(OperationEventRegistry(it)) }
        .also { it.registerEventEntries(YC.eventRegistry) }
}
