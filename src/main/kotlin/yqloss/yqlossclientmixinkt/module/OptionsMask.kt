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

import net.yqloss.uktil.accessor.Out
import net.yqloss.uktil.accessor.Ref
import net.yqloss.uktil.accessor.makeOut
import net.yqloss.uktil.accessor.makeRef
import yqloss.yqlossclientmixinkt.RT
import yqloss.yqlossclientmixinkt.ReleaseType
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0

fun requirePlus(property: KProperty0<Boolean>): Out<Boolean> = makeOut { RT >= ReleaseType.PLUS && property.get() }

fun requirePlus(property: KMutableProperty0<Boolean>): Ref<Boolean> = makeRef(
    { RT >= ReleaseType.PLUS && property.get() },
    { property.set(it) },
)

fun requireEx(property: KProperty0<Boolean>): Out<Boolean> = makeOut { RT >= ReleaseType.EX && property.get() }

fun requireEx(property: KMutableProperty0<Boolean>): Ref<Boolean> = makeRef(
    { RT >= ReleaseType.EX && property.get() },
    { property.set(it) },
)
