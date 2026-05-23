# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Snow Plows szimulator – grafikus változat (Team #48 GonoszOnosz, BME projektlabor 2026). Java/Swing GUI built on top of the same CLI-driven prototype model. This is the "grafikus" stage following an earlier text-only "prototype" stage referenced in the code/spec.

Spec source: `48_week13.docx` / `48_week13.pdf` (week 13 graphical specification).

## Build & Run

JDK only — no Maven/Gradle. The build collects every `src/**/*.java` and compiles into `bin/`.

```
build.bat                 # Windows compile (javac, UTF-8)
./build.sh                # POSIX compile
run.bat                   # Launch GUI with default 3x3 demo map
run.bat path\to\map.txt   # Launch GUI loading a CLI-format config file
./run.sh [config]         # POSIX equivalent
```

Entry points:
- `main.MainApp` — Swing GUI (default). Optional first arg = path to a CLI-format `*_in.txt` map, loaded via the `load` command.
- `main.ProtoApp` — headless CLI-only entry, reads commands from stdin. Kept working so all original CLI test scenarios still run unchanged. Run with `java -cp bin main.ProtoApp < some_in.txt`.

There is no test runner wired into the repo; original prototype-stage CLI tests live outside this directory (`prototype/tests/*_in.txt`), exercised by feeding the file to `ProtoApp` or via `Load Map…` in the GUI.

## Architecture

### Core principle: GUI is a thin layer over the CLI

Every state-changing action — from a menu click, button, or mouse drag — is translated into a CLI command string and pushed through the same `CommandParser.parseLine()` that `ProtoApp` uses. This is enforced by `controller.CommandBridge`: it exposes strongly-typed methods (`nextTurn()`, `moveSelectedTo(...)`, `buyItem(...)`, `equipHead(...)`, `forceSlip(...)`, `setRandom(...)`, etc.) that internally build the corresponding text command and hand it to the parser. **When adding GUI features, do not bypass this — route through `CommandBridge` so existing CLI test scenarios keep passing.**

`MainApp.main()` registers the same command set as `ProtoApp.main()` (`create`, `add_to_road`, `connect_roads`, `connect_fields`, `set_road_length`, `set_lane_state`, `set_money`, `set_score`, `set_salt_effect`, `spawn`, `move_bus`, `move_plow`, `buy`, `equip`, `next_turn`, `random`, `force_slip`, `stat`, `list`, `load`, `save`, `exit`). Each is an `ICommand` in `src/commands/`.

### Global context (intentional, not a smell)

`cli.Context` holds static references to the singletons (`ObjectManager`, `Determinism`, `GameLogic`, `CommandParser`). Domain classes (`Vehicle`, `Lane`, …) reach `ObjectManager` (for `id↔object` reverse lookup in event messages) and `Determinism` (for slip decisions) through `Context` instead of DI plumbing. `gameLogic` and `commandParser` are typed `Object` deliberately to avoid a `cli → model` package cycle — cast at the use site. Tests are expected to overwrite these statics.

### Packages

- `cli/` — `CommandParser`, `Context`, `ObjectManager` (LinkedHashMap, preserves insertion order so `list` and `save` are deterministic), `Determinism`, `ICommand` interface.
- `commands/` — one class per CLI verb. Each parses its `args[]` and mutates model state via `Context`.
- `model/` — pure domain. `Observable` + `IObserver` for push notifications. `Observable.notifyObservers(hint)` iterates over a **snapshot** to tolerate observer (un)subscribe during dispatch. Concrete `Observable`s: `Lane`, `Vehicle` (+ subclasses), `Player`, `GameLogic`, `HomeBase`, `IntegratedMarket`, `Building`. `GameLogic.advanceTurn()` runs: game-over check → `turnCount++` → maxTurns check → `Map.snow()` → snapshot iterate `vehicle.move()` → `manageTurn()`. The snapshot is required because `move()` mutates Lane state (slip/lock) and would otherwise CME.
- `controller/` — `CommandBridge` (GUI→CLI fan-in), `ViewBinder` (post-load wiring: walks `ObjectManager.getAll()`, creates the right `*View` per model type, subscribes it as an observer, registers with `GameRenderer`), `InputController` (mouse + keyboard on `GamePanel`), `ActionMode` enum.
- `view/` — Swing layer. `MainWindow` (JFrame, menu bar, holds `GamePanel` + `HUDPanel` + `ActionPanel`), `GameRenderer` (paint loop over registered field/road/vehicle views), `MapLayout` (pixel coordinates), dialogs (`MarketDialog`, `EndGameDialog`). Each `*View` implements `IObserver` and repaints on `update(hint)`.
- `io/OutputFormatter` — single sink for `[ERROR]` / event lines. Note: `CommandBridge.stat()` redirects `System.out` to a `ByteArrayOutputStream` to capture `stat` output for GUI tooltips/dialogs.
- `main/` — entry points + `DefaultDemo` (the canonical 3×3 grid built when `newDefaultGame()` is invoked).

### MapLayout — two layout modes

`view.MapLayout` has two distinct operating modes; mixing them carelessly will produce a broken map:

1. **Explicit**: caller (`DefaultDemo.applyLayout`) calls `placeRoadHorizontal` / `placeRoadVertical` directly with pixel coordinates. `ViewBinder.bindAll` detects this (`getAllRoadBounds().isEmpty() == false`) and calls only `placeBuildingsAndMissing()` to position the buildings/leftover fields — autoLayout is **not** run. This path exists because the heuristic autoLayout doesn't handle the 9-intersection grid.
2. **Implicit (`autoLayout`)**: triggered when roads have no pre-set bounds (i.e. after `load`). Computes positions from the `connect_fields` graph.

### Lifecycle on New Game / Load Map

`MainWindow.newDefaultGame()` and `onLoadMap()` follow the same reset sequence: `ObjectManager.clearAll()` → clear `GameLogic.players` / `vehicles`, replace `gameMap`, reset `turnCount` → `MapLayout.clear()` → run config (CLI commands for demo / `load <file>` for file) → `rebindAll()`. **Always do all four reset steps before re-running CLI config** — otherwise stale objects from the previous game stay in `ObjectManager` and `ViewBinder` will bind views to them.

### Determinism

`Determinism.shouldSlip(vehicleId)` ordering: `force_slip` override map wins; otherwise `random off` → always true (per week-7 spec), `random on` → 50% via a fixed-seed `Random(42L)` for reproducibility. `isForceSlipped` is the stricter check (explicit `true` only) used by `NextTurnCommand` to apply a pre-turn forced slip before `advanceTurn()`.

## Conventions specific to this codebase

- **Hungarian** is used throughout javadoc and inline comments — keep new comments in the same language style as the surrounding code (mostly Hungarian, no accents/diacritics in many files because of encoding caution). Source files are compiled with `-encoding UTF-8`.
- New CLI verbs require: (1) `ICommand` impl in `commands/`, (2) registration in **both** `ProtoApp.main()` and `MainApp.main()`, (3) — if GUI-reachable — a wrapper method on `CommandBridge`.
- `OutputFormatter.printError(...)` is the only correct way to surface parse/lookup errors; do not write directly to `System.out`/`System.err` from command code (the GUI captures stdout via `CommandBridge.stat`).
- Mutating list iterations during dispatch: follow the existing snapshot pattern (`new ArrayList<>(...)`) used in `Observable.notifyObservers` and `GameLogic.advanceTurn`.

## User-specific operational notes

- Working directory is Windows; the shell tool here is PowerShell — use PowerShell syntax (`$env:VAR`, `;` chaining, no `&&`). Bash is available for POSIX-only needs.
- Per the user's global rule: **never use mock data/responses unless explicitly told to**; if a mock is unavoidable for a demo, call it out explicitly.
- Per the user's global rule: use **complete absolute Windows paths with drive letter and backslashes** for all file operations against this repo.
