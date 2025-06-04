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

@file:Suppress("NOTHING_TO_INLINE")

package yqloss.yqlossclientmixinkt.util.accessor

import kotlin.reflect.KProperty

interface Ref<T> :
    In<T>,
    Out<T>

inline var <T> Ref<T>.value
    get() = get()
    set(value) = set(value)

inline operator fun <X, T> Ref<T>.getValue(
    thisRef: X,
    property: KProperty<*>,
) = value

inline operator fun <X, T> Ref<T>.setValue(
    thisRef: X,
    property: KProperty<*>,
    value: T,
) = set(value)

inline infix fun <T> Ref<T>.swap(ref: Ref<T>) {
    val v1 = get()
    val v2 = ref.get()
    set(v2)
    ref.set(v1)
}
