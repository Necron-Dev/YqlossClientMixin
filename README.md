# Repository Archived

退游出号了，项目终止开发于 2.1.0 版本。

不会做高版本移植，因为：
* 我不会。我从来没有开发过高版本 Mod。我可以说对 1.8.9 的反编译源码了如指掌，但是对高版本一无所知
* 我主玩地牢，在主玩 1.8.9 的前提下，个人感觉高版本手感远不如低版本，不喜欢用高版本玩地牢
* 没有空余时间，没有开发动力，没有账号测试
* OneConfig 库正在被重写，而本项目中包含许多关于 OneConfig 的黑魔法，重新适配略有难度
* 如果放弃 OneConfig 的话有两条路可以走：自己加载 NanoVG 或者选择其他 2D 渲染库并重写 GUI，显然哪个都不容易
* Minecraft 从低版本到高版本的改动也较大，尤其是对于本项目这种从来没有考虑过高版本适配的 Mod，需要调整很多东西才能适配
* 已经有（或者马上就会有）更优秀的替代品

Hypixel 是否会完全放弃 1.8.9 目前还不得而知。
即使他们真的放弃了低版本，本项目也不会继续开发了，这个游戏我也不想玩了。

### English Translated By Gemini-3 Flash Preview:

I’m officially quitting Skyblock and selling my account. Development of this project has ceased as of **Version 2.1.0**.

There are no plans to port the mod to modern versions (1.20+), for the following reasons:
* **Lack of Experience:** I have never developed mods for modern versions. While I know the 1.8.9 decompiled source code inside out, I am completely unfamiliar with modern Minecraft’s architecture.
* **Gameplay Feel:** I am a Dungeons main. Personally, I find the mechanics and "feel" of modern versions to be far inferior to 1.8.9, and I have no interest in playing Dungeons on newer versions.
* **Lack of Resources:** I no longer have the time, the motivation, or even a test account to continue development.
* **OneConfig Dependency:** OneConfig is currently being rewritten. This project relies on a lot of "black magic" (hacks) involving OneConfig that would be incredibly difficult to refactor for the new version.
* **GUI Challenges:** If I were to drop OneConfig, I would either have to manually implement NanoVG or switch to a different 2D rendering library and rewrite the entire GUI—neither of which is a small feat.
* **Structural Changes:** The jump from legacy to modern Minecraft involves massive changes. Since this mod was never designed with cross-version compatibility in mind, porting it would require a complete overhaul.
* **Better Alternatives:** Superior alternatives already exist (or are about to be released).

It remains to be seen whether Hypixel will eventually drop 1.8.9 support. However, even if they do, I will not be returning to this project. I'm moving on from the game for good.

# Yqloss Client (Mixin)

![Yqloss Client (Mixin)](https://socialify.git.ci/Necron-Dev/YqlossClientMixin/image?description=1&font=Raleway&forks=1&issues=1&logo=https%3A%2F%2Fraw.githubusercontent.com%2Fboopwdn%2FYqlossClientMixin%2Frefs%2Fheads%2Fmaster%2Ficon.svg&name=1&owner=1&pulls=1&stargazers=1&theme=Auto)

Permanent Release Link: https://get.yqloss.net

Showcase Video: https://www.bilibili.com/video/BV1Q4wfejEAB

Note that this mod is not registered as a Forge mod, nor do it use Forge events or API. It's all based on Mixin.

## Internationalization

* This mod supports multi-language.
* Currently two languages are supported: en_US and zh_CN
* If you find any mistakes or have better translations, feel free to make an issue!


* 本模组支持多语言
* 目前支持两种语言：英语、简体中文
* 如果您发现了翻译错误或有更好的翻译，欢迎提交 Issue

## Feature Legitimacy

* Normal Version (without suffix): purely visual features only
* PLUS Version: purely visual features, macros and features that are possible on vanilla in the server's view
* EX version: all features

Configurations will NOT be lost switching between different legitimacy versions!

## Feature List

* Better Terminal (queue clicks; drag click)
* Corpse Finder (mineshaft)
* Map Marker (purely visual block replacement)
* Mining Prediction (shows when you should aim at the next block)
* Raw Input (JInput / Native Win32 implementations)
* SS Motion Blur (based on screenshot instead of shader; can be used with Fast Render; little performance impact)
* YC Leap Menu (5-grid ring-shaped menu; differs from most leap menus; leap hotkeys)
* Window Properties (borderless window; windowed fullscreen; custom window title)
* Hotkeys (separate keys for drop single item / item stack)
* Cursor (cursor trail)
* Tweaks (features that modify vanilla slightly)
    * Enable Instant Aim (fix aiming being delayed for 1 tick)
    * Disable Pearl Click-On-Block Packet (commonly called Cancel Interact; makes you able to throw pearls while aiming
      at a block on most SkyBlock islands)
    * Disable NBT Update Reset Digging on SkyBlock Mining Islands (a feature backport from new versions of Minecraft)

## Dependency

* [OneConfig](https://github.com/Polyfrost/OneConfig)

### Soft Dependency

* [Hypixel Mod API](https://github.com/HypixelDev/ModAPI)

## Special Thanks

* [ench](https://github.com/EnchStudio): GUI design and ideas
* trytoquit: lend me a Puzzle Cube for testing BetterTerminal
* All early version testers (and those who crashed because of my mod)

## License

This project is licensed under the **GNU General Public License v2.0 (GPLv2)**.  
See the [LICENSE](LICENSE) file for details.

<details>

<summary>Copyright</summary>

This mod is based on [OneConfigExampleMod](https://github.com/Polyfrost/OneConfigExampleMod)

* Copyright (C) 2025 Yqloss ([GPLv2 License](LICENSE))
* Raw Input: Copyright (c) 2020
  Curi0 ([Project](https://github.com/xCuri0/RawInputMod)) ([MIT License](LICENSE_RAW_INPUT))
* Montserrat Font: Copyright 2024 The Montserrat.Git Project
  Authors (https://github.com/JulietaUla/Montserrat.git) ([Project](https://github.com/JulietaUla/Montserrat)) ([OFL License](src/main/resources/assets/yqlossclientmixin/font/montserrat/OFL.txt))
* Open Color (Default Color Scheme): Copyright (c) 2016
  heeyeun ([Project](https://github.com/yeun/open-color)) ([MIT License](LICENSE_OPEN_COLOR))
* NotoSans SC Font: Copyright 2014-2021 Adobe (http://www.adobe.com/), with Reserved Font Name
  'Source' ([OFL License](src/main/resources/assets/yqlossclientmixin/font/notosans_sc/OFL.txt))

At the same time, for better user experience and compatibility, I included the following libraries in
the [libraries](libraries) folder:

* The internal part of OneConfig (for accessing NanoVG): Copyright (C) 2021-2024 Polyfrost Inc. and
  contributors. ([Project](https://github.com/Polyfrost/OneConfig)) ([MIT License](libraries/LICENSE_ONECONFIG))
* Some OptiFine classes exported from running Minecraft (for compatibility)

</details>

## Other Things I Want To Say

If you encounter a bug that crashes your game and can be stably reproduced, please report in issues and I'll fix as soon
as I can.

Feature requests will NOT be accepted easily because I made this mod for myself. I won't add features I don't need or
there's a better option for the feature in every aspect. If you would like to commit a pull request, please ask if I
would merge in the issues before you actually do that. (Feel free to fork as long as you follow GPLv2)

To code this mod, I learned many new things for the first time, such as Kotlin, Mixin, Gradle, Git, GUI design and so
on. Maybe it's not good, but I'm trying my best. Hope you enjoy.

## Links

[EnchAddons](https://github.com/Necron-Dev/EnchAddons) by EnchStudio

[HypixelHelper](https://github.com/SuperShadiao/hypixelhelper) by SuperShadiao
