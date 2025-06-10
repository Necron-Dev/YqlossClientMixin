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

package yqloss.yqlossclientmixinkt.network

import kotlinx.coroutines.*
import net.yqloss.uktil.accessor.outs.Box
import net.yqloss.uktil.accessor.outs.inBox
import yqloss.yqlossclientmixinkt.util.NETWORK_ALWAYS_FAIL_REQUEST

open class SuspendTypedResource<T>(
    private val function: suspend () -> T,
) : TypedResource<T> {
    override var requesting = false

    override var data: Box<T>? = null

    override var onAvailable: (() -> Unit)? = null

    override var onTypedAvailable: ((T) -> Unit)? = null

    override fun request() {
        synchronized(this) {
            requesting && return
            requesting = true
        }
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                NETWORK_ALWAYS_FAIL_REQUEST && throw Exception("NETWORK_ALWAYS_FAIL_REQUEST")
                data = function()
                    .also {
                        onAvailable?.invoke()
                        onTypedAvailable?.invoke(it)
                    }.inBox
            } catch (exception: Exception) {
                networkLogger.warn("exception executing SuspendTypedResource", exception)
            } finally {
                synchronized(this@SuspendTypedResource) {
                    requesting = false
                }
            }
        }
    }
}
