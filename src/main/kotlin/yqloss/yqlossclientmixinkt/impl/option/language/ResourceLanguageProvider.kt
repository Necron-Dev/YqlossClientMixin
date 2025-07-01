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

package yqloss.yqlossclientmixinkt.impl.option.language

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*
import net.yqloss.uktil.accessor.outs.Box
import net.yqloss.uktil.accessor.outs.inBox
import net.yqloss.uktil.accessor.outs.value
import net.yqloss.uktil.event.EventRegistration
import net.yqloss.uktil.event.EventRegistry
import net.yqloss.uktil.event.register
import net.yqloss.uktil.scope.longRet
import net.yqloss.uktil.scope.noExcept
import net.yqloss.uktil.scope.usingScope
import yqloss.yqlossclientmixinkt.YCJson

class ResourceLanguageProvider(private val clazz: Class<*>) : EventRegistration {
    private val languageMap = mutableMapOf<String, JsonElement>()

    private val translationMap = mutableMapOf<String, Box<String?>>()

    @OptIn(ExperimentalSerializationApi::class)
    private fun loadLanguage(language: String): JsonElement? {
        languageMap[language]?.let { return it }

        return noExcept {
            usingScope {
                YCJson.decodeFromStream<JsonElement>(
                    clazz.getResourceAsStream("/assets/yqlossclientmixin/language/$language.json")!!.using,
                )
            }
        }?.also { languageMap[language] = it }
    }

    private fun getTranslation(language: JsonElement, translationKey: String): String? {
        val split = translationKey.split('.')
        var element = language
        split.forEach { name ->
            element = when (val element = element) {
                is JsonObject -> element[name]
                is JsonArray -> name.toIntOrNull()?.let { element.getOrNull(it) }
                else -> null
            } ?: return null
        }
        return when (val element = element) {
            is JsonPrimitive -> element.content
            is JsonArray -> element.mapNotNull { (it as? JsonPrimitive)?.content }.joinToString("\n")
            else -> null
        }
    }

    override val registerEvents: EventRegistry.() -> Unit
        get() = {
            super.registerEvents(this)

            register<LanguageEvent.Translate> { event ->
                event.canceled && longRet

                val translation = translationMap[event.string] ?: run {
                    loadLanguage(event.language)?.let {
                        getTranslation(it, event.string)
                    } ?: loadLanguage(event.fallbackLanguage)?.let {
                        getTranslation(it, event.string)
                    }
                }.inBox.also { translationMap[event.string] = it }

                translation.value?.let {
                    event.mutableString = it
                    event.canceled = true
                }
            }
        }
}
