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
- `GameSettings.java` — difficulty and joystick-size options, read every frame

`app/src/main/java/com/chuck/rpggame/` — Android layer:
- `GameView.java` — `SurfaceView` + game loop thread + rendering + touch input,
  driven by a `GameState` (`TITLE`, `PLAYING`, `PAUSED`, `SETTINGS`, `GAME_OVER`)
- `TitleScreen.java`, `PauseMenu.java`, `SettingsScreen.java`, `GameOverScreen.java`
  — one small class per screen, each just does hit-testing + drawing for itself
- `VirtualJoystick.java` — on-screen movement stick
- `InventoryPanel.java` — tap-to-equip overlay list
- `SpriteSheet.java` — slices a bitmap grid into frames; frame index is a pure function of time
- `PlaceholderArt.java` — draws a runtime placeholder character sheet (see below)
- `MainActivity.java` — hosts `GameView` fullscreen, nothing else

Keeping `core/` free of `android.*` imports means the game rules can be reasoned
about (and eventually unit-tested) independent of the Android SDK.

## Current gameplay

- **Title screen**: New Game / Settings / Exit.
- Drag the bottom-left stick to move. ATK melees anything in range. Three skill
  buttons (Power Strike, Whirlwind, Heal) each cost SP and have their own
  cooldown — buttons dim and count down while unavailable.
- **II** (pause button, top area) freezes the game and opens Resume / Settings / Quit to Title.
- **BAG** opens the inventory — enemies have a chance to drop gear on death; tap
  an item to equip it (weapon/armor/accessory add attack, defense, or max HP).
- **Settings** (from title or pause): Difficulty (Easy/Normal/Hard, scales enemy
  damage and spawn rate) and Joystick size — tap either to cycle.
- Dying now actually does something: HP hitting 0 shows a "You Died" screen
  with the level you reached, Respawn or Quit to Title. Before this pass the
  player just sat at 0 HP with nothing happening — this was a real gap, not a
  style choice.

## About the art

Still no real art — `PlaceholderArt` now draws a slightly more readable
character than the first pass (lighter head shade so it reads as a head rather
than blending into the body, two eye dots, a chest stripe), but it's still
procedural placeholder, not actual pixel art, sliced into 4 frames for a walk
bob so the real `SpriteSheet` + `Canvas.drawBitmap` pipeline is genuinely
exercised. I can't generate real image files in this environment.

Fastest real path: **Kenney.nl** has free, public-domain (CC0, no attribution
needed) top-down RPG character/tile packs — search "RPG" on kenney.nl/assets.
Download on your phone, pull the PNGs into the project, then in
`GameView.initSprites()` swap the two `PlaceholderArt.generateCharacterSheet(...)`
calls for `BitmapFactory.decodeResource(getResources(), R.drawable.your_sheet)`.
Two things that matter when you do:
1. Put the PNG in `res/drawable-nodpi/`, not plain `res/drawable/` — the nodpi
   qualifier stops Android from auto-scaling it for screen density, which
   would otherwise throw off `SpriteSheet`'s exact-pixel frame slicing.
2. Tell me the frame width/height and frame count of whatever sheet you get
   (or draw) and I'll wire up the exact `SpriteSheet` call — the current one
   assumes a single row of 4 equal-width frames, which your asset may not match.

## Building it

**GitHub Actions (recommended — same pattern as the Capacitor/AnimeCorn build):**
push this to a repo and `.github/workflows/android-build.yml` builds a debug APK
and uploads it as a workflow artifact. No local Android SDK required. This has
now been confirmed working — you've built and run this project on-device.

**Locally:** needs JDK 17+ and the Android SDK. From the project root, run
`gradle wrapper --gradle-version 8.9` once, then `./gradlew assembleDebug`.

## Next steps (pick a direction)

- Real sprite sheets once you've got art (see "About the art")
- More enemy types / a boss with its own attack pattern
- Tile-based maps instead of the open arena
- Unequip UI (the `Player.equip*(null)` API already supports it)
- Consumable items (potions) — `Item.Type.CONSUMABLE` exists but isn't used yet
- Persist player state / settings across app restarts (everything currently
  resets on launch — `GameSettings` in particular resets to defaults every
  time the app process restarts, since it's in-memory only)
- Sound effects and music (nothing audio-related exists yet, which is why
  Settings doesn't have a volume option — would rather add it when it means something)
