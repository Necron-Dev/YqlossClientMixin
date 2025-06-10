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

package yqloss.yqlossclientmixinkt.util

import net.yqloss.uktil.math.Vec3D
import org.lwjgl.opengl.GL11.*
import yqloss.yqlossclientmixinkt.module.option.YCColor
import java.lang.AutoCloseable

val glMatrixElement: AutoCloseable
    get() {
        glPushMatrix()
        return AutoCloseable {
            glPopMatrix()
        }
    }

inline fun <R> glMatrixScope(function: () -> R) {
    glPushMatrix()
    try {
        function()
    } finally {
        glPopMatrix()
    }
}

val glAttribElement: AutoCloseable
    get() {
        glPushAttrib(GL_ALL_ATTRIB_BITS)
        glPushClientAttrib(GL_ALL_CLIENT_ATTRIB_BITS)
        return AutoCloseable {
            glPopClientAttrib()
            glPopAttrib()
        }
    }

inline fun <R> glAttribScope(function: () -> R) {
    glPushAttrib(GL_ALL_ATTRIB_BITS)
    glPushClientAttrib(GL_ALL_CLIENT_ATTRIB_BITS)
    try {
        function()
    } finally {
        glPopClientAttrib()
        glPopAttrib()
    }
}

val glStateElement: AutoCloseable
    get() {
        glPushAttrib(GL_ALL_ATTRIB_BITS)
        glPushClientAttrib(GL_ALL_CLIENT_ATTRIB_BITS)
        glPushMatrix()
        return AutoCloseable {
            glPopMatrix()
            glPopClientAttrib()
            glPopAttrib()
        }
    }

inline fun <R> glStateScope(function: () -> R) {
    glPushAttrib(GL_ALL_ATTRIB_BITS)
    glPushClientAttrib(GL_ALL_CLIENT_ATTRIB_BITS)
    glPushMatrix()
    try {
        function()
    } finally {
        glPopMatrix()
        glPopClientAttrib()
        glPopAttrib()
    }
}

inline fun Vec3D.glTranslate() {
    glTranslated(x, y, z)
}

inline fun YCColor.glColor() {
    glColor4d(r, g, b, a)
}
