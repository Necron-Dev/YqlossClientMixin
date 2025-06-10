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

@file:Suppress("UnstableApiUsage", "PropertyName")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.polyfrost.gradle.util.noServerRunConfigs

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.1.0"
    id("org.polyfrost.multi-version")
    id("org.polyfrost.defaults.repo")
    id("org.polyfrost.defaults.java")
    id("org.polyfrost.defaults.loom")
    id("com.github.johnrengelman.shadow")
    id("net.kyori.blossom") version "1.3.2"
    id("signing")
    java
}

val mod_name: String by project
val mod_version: String by project
val mod_id: String by project
val mod_archives_name: String by project
val mod_group: String by project

blossom {
    replaceToken("@VER@", mod_version)
    replaceToken("@NAME@", mod_name)
    replaceToken("@ID@", mod_id)
}

version = mod_version
group = mod_group

base {
    archivesName.set("$mod_archives_name-$platform")
}

loom {
    noServerRunConfigs()

    runConfigs {
        "client" {
            programArgs("--tweakClass", "cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker")
            property("mixin.debug.export", "true")
        }
    }

    forge {
        mixinConfig("mixins.${mod_id}.json")
    }

    mixin.defaultRefmapName.set("mixins.${mod_id}.refmap.json")
}

val shade: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

val modShade: Configuration by configurations.creating {
    configurations.modImplementation.get().extendsFrom(this)
}

sourceSets {
    val dummy by creating
    main {
        dummy.compileClasspath += compileClasspath
        compileClasspath += dummy.output
        output.setResourcesDir(java.classesDirectory)
    }
}

repositories {
    mavenCentral()
    maven("https://maven.yqloss.net")
    maven("https://repo.polyfrost.org/releases")
    maven("https://repo.hypixel.net/repository/Hypixel/")
}

dependencies {
    modCompileOnly("cc.polyfrost:oneconfig-$platform:0.2.2-alpha+")
    modRuntimeOnly("me.djtheredstoner:DevAuth-forge-legacy:1.2.0")
    compileOnly("org.spongepowered:mixin:0.7.11-SNAPSHOT")
    shade("cc.polyfrost:oneconfig-wrapper-launchwrapper:1.0.0-beta17")
    implementation("net.hypixel:mod-api:1.0.1")
    shade("org.antlr:ST4:4.3.4")
    shade("com.squareup.okhttp3:okhttp:4.12.0")
    shade("com.squareup.okhttp3:okhttp-coroutines:5.0.0-alpha.14")
    compileOnly("org.lwjgl:lwjgl:3.3.1")
    compileOnly("org.lwjgl:lwjgl-nanovg:3.3.1")
    compileOnly(files("${project.rootDir}/libraries/oneconfig-internal.jar"))
    compileOnly(files("${project.rootDir}/libraries/optifine-compatibility.jar"))
    shade("net.yqloss:uktil:0.4.0")
}

tasks {
    processResources {
        inputs.property("id", mod_id)
        inputs.property("name", mod_name)
        inputs.property("java", 8)
        inputs.property("java_level", "JAVA_8")
        inputs.property("version", mod_version)
        inputs.property("mcVersionStr", project.platform.mcVersionStr)
        filesMatching(listOf("mcmod.info", "mixins.${mod_id}.json")) {
            expand(
                mapOf(
                    "id" to mod_id,
                    "name" to mod_name,
                    "java" to 8,
                    "java_level" to "JAVA_8",
                    "version" to mod_version,
                    "mcVersionStr" to project.platform.mcVersionStr,
                ),
            )
        }
    }

    named<ShadowJar>("shadowJar") {
        archiveClassifier.set("dev")
        configurations = listOf(shade, modShade)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    remapJar {
        inputFile.set(shadowJar.get().archiveFile)
        archiveClassifier.set("")
    }

    jar {
        if (platform.isLegacyForge) {
            manifest.attributes +=
                mapOf(
                    "ModSide" to "CLIENT",
                    "ForceLoadAsMod" to true,
                    "TweakOrder" to "0",
                    "MixinConfigs" to "mixins.${mod_id}.json",
                    "TweakClass" to "cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker",
                )
        }
        dependsOn(shadowJar)
        archiveClassifier.set("")
        enabled = false
    }
}
