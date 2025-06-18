# Yqloss Client (Mixin) Changelog

## 0.5.0 (UTC+8 2025/02/18 00:30)

* Yqloss Client (Mixin) is released to public
* All previous changelogs have been deleted

## 0.5.1 (UTC+8 2025/02/18 02:00)

* fix(BetterTerminal): fix a game crashing bug though the cause and reproducing method haven't been found

## 0.5.2 (UTC+8 2025/02/19 01:30)

* fix: fix being unable to proxy screen with only OptiFine installed

## 0.5.3 (UTC+8 2025/02/19 21:00)

* fix(MiningPrediction): fix HUD showing up when you break a block with the HUD disabled

## 0.6.0 (UTC+8 2025/02/19 21:50)

* feat(BetterTerminal): add "show number" option for Order and Rubix terminal

## 0.7.0 (UTC+8 2025/02/21 04:40)

* feat(MapMarker): init
* feat(YCLeapMenu): add option for smooth GUI
* feat: add # before and after mod name
* feat: add command /yc to open config menu
* fix: fix button order in config menu being messed up
* fix: fix fade in/out elements not showing up when smooth GUI is disabled
* fix: should have fixed crashing because of IBlockAccess wrapping in some cases, though the cause is not clear
* fix: fix incompatibility with PolyPatcher's GUI scale override by modifying mixin priority

## 0.8.0 (UTC+8 2025/04/05 23:10)

* feat: add a button to load all characters, solving the lag spike issue of SmoothFont during gameplay
* fix(MapMarker): fix not reloading world when room type changes in dungeons
* feat: add mcmod.info to be recognized in launchers
* feat(WindowProperties): init (Windowed Fullscreen, Borderless Window, Custom Window Title)
* feat(RawInput): implement native raw input for Windows AMD64
* feat(BetterTerminal): add option rubixCorrectDirection
* feat(BetterTerminal): add separate enable and smooth GUI option for each type of terminal
* fix(BetterTerminal): fix being unable to queue Order
* fix(WindowProperties, RawInput): fix mouse not centered when opening GUI

## 0.8.1 (UTC+8 2025/04/07 23:30)

* fix(WindowProperties, RawInput): fix mouse not centered when opening GUI
* feat(YCLeapMenu): add support for Haunt

## 0.8.2 (UTC+8 2025/04/11 21:30)

* fix(WindowProperties): fix window being resized to 1920x1080 at startup
* feat(MiningPrediction): make the feature unavailable when Mining Speed is not present in tab

## 0.9.0 (UTC+8 2025/04/21 20:40)

* refactor(BetterTerminal): rewrite some of BetterTerminal for myself to better understand what I was doing
* fix(BetterTerminal): fix clicks being delayed 1-2 more ticks than expected
* fix(BetterTerminal): fix terminal being reset in rubix and panes when misclick

## 0.10.0 (UTC+8 2025/05/09 07:00)

* fix: OneConfig and YC GUI Chinese font
* fix(RawInput): fix incompatibility with mods that inject MouseHelper
* fix(RawInput): fix mouse being locked with native enabled but the feature disabled
* fix(MapMarker): ignore unknown keys
* feat(Hotkeys): init (drop single item, drop item stack)
* feat(BetterTerminal): drag click
* feat(YCLeapMenu): leap hotkeys
* feat(SSMotionBlur): add linear balanced mode
* feat(Repository): init (update checker, custom capes)

## 0.11.0 (UTC+8 2025/05/16 20:30)

* refactor: improve the performance of IBlockAccess wrapping
* fix: add nullability check for vanilla IBlockState.getBlockState
* fix(WindowProperties): fix taking effect even if the module is not enabled
* feat(Cursor): init (cursor trail)

## 0.11.1 (UTC+8 2025/05/18 11:00)

* fix: set most GL states after rendering GUI
* fix(MiningPrediction): use server tick
* fix(RawInput): generate a separate dll for every Minecraft instance

## 0.11.2 (UTC+8 2025/05/30 02:00)

* feat(Repository): request cooldown
* feat(Cursor): add Hide When Not Moving

## 1.0.0 (UTC+8 2025/06/10 19:10)

* fix(Repository): fix saying "an update is available" when using a dev version; close #25
* BREAKING CHANGE: extract the util part to a separate library
* and I think it's time to release 1.0.0

## 2.0.0 ()

* Starting from 2.0.0 there will be 2 versions, normal and EX
* All illegal features (including macros) will be removed in the normal version
* feat(Tweaks): add Catacombs boss bar fix
