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

package yqloss.yqlossclientmixinkt.impl.api

import net.yqloss.uktil.scope.noExcept
import org.stringtemplate.v4.*
import yqloss.yqlossclientmixinkt.api.YCTemplate
import java.util.*

private val cache = WeakHashMap<String, ST>()

private val group = STGroup('<', '>').apply {
    registerRenderer(Number::class.java, NumberRenderer())
    registerRenderer(String::class.java, StringRenderer())
    registerRenderer(Date::class.java, DateRenderer())
}

class YCTemplateImpl(
    private val template: String,
) : YCTemplate {
    private val st = noExcept {
        ST(
            cache.getOrPut(template) {
                ST(group, template)
            },
        )
    }

    override fun set(
        key: String,
        value: Any?,
    ) {
        st?.add(key, value)
    }

    // ST4 replaces \n with \r\n for some reason
    override fun format() = (noExcept { st?.render() } ?: template).replace("\r\n", "\n")
}
