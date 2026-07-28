# RPG Game (working title)

Native Android action-RPG prototype in Java, inspired by Zenonia (real-time combat,
leveling, gear-driven progression, active skills). No external game engine — a
`SurfaceView` + game-loop thread, `Canvas`/`Paint`/`Bitmap` drawing, and virtual
on-screen touch controls.

## Structure

`app/src/main/java/com/chuck/rpggame/core/` — pure Java game logic, zero Android imports:
- `Entity.java` — base position/hp/collision
- `Player.java` — movement, leveling, equipment bonuses, SP, inventory, skills
- `Enemy.java` — chase AI, contact damage
- `GameWorld.java` — spawning, per-frame updates, melee/skill hit detection, loot drops
- `Item.java`, `Inventory.java`, `ItemFactory.java` — gear and loot
- `Skill.java` (+ `PowerStrikeSkill`, `WhirlwindSkill`, `HealSkill`) — active abilities

`app/src/main/java/com/chuck/rpggame/` — Android layer:
- `GameView.java` — `SurfaceView` + game loop thread + rendering + touch input
- `VirtualJoystick.java` — on-screen movement stick
- `InventoryPanel.java` — tap-to-equip overlay list
- `SpriteSheet.java` — slices a bitmap grid into frames; frame index is a pure function of time
- `PlaceholderArt.java` — draws a runtime placeholder character sheet (see below)
- `MainActivity.java` — hosts `GameView` fullscreen, nothing else

Keeping `core/` free of `android.*` imports means the game rules can be reasoned
about (and eventually unit-tested) independent of the Android SDK.

## Current gameplay

- Drag the bottom-left stick to move.
- ATK (bottom-right) melees anything in range, short cooldown.
- Three skill buttons left of ATK: Power Strike (heavy single hit), Whirlwind
  (wide AoE), Heal. Each costs SP (regenerates slowly) and has its own cooldown —
  buttons dim and show a countdown while unavailable.
- BAG (top-right) opens/closes the inventory. Enemies have a chance to drop gear
  on death; tap an item in the list to equip it (weapon/armor/accessory — each
  adds attack, defense, or max HP respectively). The world pauses while the
  panel is open.
- Leveling up raises max HP and strength; equipped gear stacks on top of that.

## About the art

There's no real art yet — `PlaceholderArt` draws a simple procedural
"body + head + accent stripe" character into a real `Bitmap` at runtime, sliced
into 4 frames for a basic walk bob, so the actual sprite-sheet rendering path
(`SpriteSheet` + `Canvas.drawBitmap`) is fully wired and exercised, not just
stubbed out. I can't generate real pixel art in this environment — when you
have actual sprite sheets (drawn, commissioned, or from an asset pack), swap
the two `PlaceholderArt.generateCharacterSheet(...)` calls in
`GameView.initSprites()` for
`BitmapFactory.decodeResource(getResources(), R.drawable.your_sheet)`.
Frame slicing, walk-cycle timing, and the left/right flip all keep working unchanged.

## Building it

**GitHub Actions (recommended — same pattern as the Capacitor/AnimeCorn build):**
push this to a repo and `.github/workflows/android-build.yml` builds a debug APK
and uploads it as a workflow artifact. No local Android SDK required.

**Locally:** needs JDK 17+ and the Android SDK. From the project root, run
`gradle wrapper --gradle-version 8.9` once, then `./gradlew assembleDebug`.

Note: none of this has been build-tested — the sandbox this was written in has
a JRE but no javac and no network, so the CI run is the first real compile.
A good amount of new code went in this pass (skills, equipment, sprites all at
once), so if the build breaks, paste the error and it's a quick fix — but this
is the point where testing before piling on more features starts to pay off.

## Next steps (pick a direction)

- Real sprite sheets once you've got art (see "About the art")
- More enemy types / a boss with its own attack pattern
- Tile-based maps instead of the open arena
- Unequip UI (the `Player.equip*(null)` API already supports it)
- Consumable items (potions) — `Item.Type.CONSUMABLE` exists but isn't used yet
- Persist player state across app restarts (everything currently resets on launch)
