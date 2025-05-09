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

package yqloss.yqlossclientmixinkt.event.impl

import yqloss.yqlossclientmixinkt.event.YCEvent
import yqloss.yqlossclientmixinkt.event.YCEventDispatcher
import yqloss.yqlossclientmixinkt.event.YCEventHandler
import yqloss.yqlossclientmixinkt.event.YCEventRegistry
import yqloss.yqlossclientmixinkt.util.HashComparator
import yqloss.yqlossclientmixinkt.util.UniqueHash
import yqloss.yqlossclientmixinkt.util.extension.castTo
import yqloss.yqlossclientmixinkt.util.extension.type.prepend
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSuperclassOf

class EventManager :
    YCEventDispatcher,
    YCEventRegistry {
    private val lock = ReentrantReadWriteLock()

    private val hashComparator = HashComparator<YCEventHandler<*>>(UniqueHash())

    private class RegistryEntry(
        val hashComparator: HashComparator<YCEventHandler<*>>,
        val priority: Int,
        val handler: YCEventHandler<*>,
    ) : Comparable<RegistryEntry> {
        override fun compareTo(other: RegistryEntry): Int {
            return if (handler === other.handler) {
                0
            } else {
                priority
                    .compareTo(other.priority)
                    .takeIf { it != 0 }
                    ?: hashComparator.compare(handler, other.handler)
            }
        }

        override fun equals(other: Any?): Boolean {
            return (other as? RegistryEntry)?.let {
                handler === other.handler
            } == true
        }

        override fun hashCode() = handler.hashCode()
    }

    private val parentTypeHandlerMap = mutableMapOf<KClass<*>, TreeSet<RegistryEntry>>()

    private val onlyTypeHandlerMap = mutableMapOf<KClass<*>, TreeSet<RegistryEntry>>()

    private val parentHandlerCache = mutableMapOf<KClass<*>, YCEventHandler<*>>()

    private val onlyHandlerCache = mutableMapOf<KClass<*>, YCEventHandler<*>>()

    private fun clearAllCache() {
        parentHandlerCache.clear()
        onlyHandlerCache.clear()
    }

    private fun clearCache(type: KClass<*>) {
        parentHandlerCache.entries.removeIf { type.isSuperclassOf(it.key) }
        onlyHandlerCache.entries.removeIf { type.isSuperclassOf(it.key) }
    }

    override fun <T : YCEvent> register(
        type: KClass<T>,
        priority: Int,
        handler: YCEventHandler<T>,
    ) {
        lock.write {
            clearCache(type)
            unregister(handler)
            parentTypeHandlerMap
                .getOrPut(type, ::TreeSet)
                .add(RegistryEntry(hashComparator, priority, handler))
        }
    }

    override fun <T : YCEvent> registerOnly(
        type: KClass<T>,
        priority: Int,
        handler: YCEventHandler<T>,
    ) {
        lock.write {
            clearCache(type)
            unregisterOnly(handler)
            onlyTypeHandlerMap
                .getOrPut(type, ::TreeSet)
                .add(RegistryEntry(hashComparator, priority, handler))
        }
    }

    private fun unregisterIn(
        handler: YCEventHandler<*>,
        registry: () -> Sequence<Map.Entry<KClass<*>, TreeSet<RegistryEntry>>>,
    ) {
        lock.write {
            registry().forEach { (type, entries) ->
                if (entries.removeIf { it.handler === handler }) {
                    clearCache(type)
                }
            }
        }
    }

    override fun unregister(handler: YCEventHandler<*>) {
        unregisterIn(handler) { parentTypeHandlerMap.entries.asSequence() }
    }

    override fun unregisterOnly(handler: YCEventHandler<*>) {
        unregisterIn(handler) { onlyTypeHandlerMap.entries.asSequence() }
    }

    override fun unregisterAll(handler: YCEventHandler<*>) {
        unregisterIn(handler) { parentTypeHandlerMap.entries.asSequence() + onlyTypeHandlerMap.entries.asSequence() }
    }

    override fun clear() {
        lock.write {
            clearAllCache()
            parentTypeHandlerMap.clear()
            onlyTypeHandlerMap.clear()
        }
    }

    override fun <T : YCEvent> getHandler(type: KClass<T>): YCEventHandler<T> {
        lock.read {
            return parentHandlerCache
                .getOrPut(type) {
                    lock.write {
                        val set = TreeSet<RegistryEntry>()
                        type.allSuperclasses
                            .prepend(type)
                            .filter { it.isSubclassOf(YCEvent::class) }
                            .forEach { parentTypeHandlerMap[it]?.let(set::addAll) }
                        onlyTypeHandlerMap[type]?.let(set::addAll)
                        EventHandlerHolder(set.toList().map { it.handler })
                    }
                }.castTo()
        }
    }

    override fun <T : YCEvent> getHandlerOnly(type: KClass<T>): YCEventHandler<T> {
        lock.read {
            return onlyHandlerCache
                .getOrPut(type) {
                    lock.write {
                        val set = TreeSet<RegistryEntry>()
                        parentTypeHandlerMap[type]?.let(set::addAll)
                        onlyTypeHandlerMap[type]?.let(set::addAll)
                        EventHandlerHolder(set.toList().map { it.handler })
                    }
                }.castTo()
        }
    }
}
