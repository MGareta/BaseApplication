---
name: eval-mechanical
description: Run the deterministic gate checks (OpenSpec validation, import allowlist, forbidden symbols, traceability, module wiring, Gradle) for an orchestrated feature change and return a verdict JSON. Use in eval agents and the coordinator before any judged review.
---

# eval-mechanical

Runs `scripts/orchestrator/eval-mechanical.py`, which implements the mechanical rules of `docs/orchestrator/proposal.md` §7.3.

## Usage

```bash
python3 -I scripts/orchestrator/eval-mechanical.py --change <change-id> --feature <feature> --gate <gate> [--gradle] [--base <git-ref>]
```

| Gate | Checks |
|---|---|
| `preflight` | OpenSpec 1.15.x; prerequisites P1 (Navigation3 Hilt ViewModel factory) and P2 (feature plugin test setup); allowlist freshness |
| `G0a` | `openspec validate --strict`; `isPlanningComplete`; every task has a role tag and `Req:`; every scenario has a test task; only the four expected capabilities |
| `G0c` | module build files (plugin, namespace, no external deps, presentation → data, no own `:common:navigation` edge: the plugin adds it), `settings.gradle.kts` includes, `:app` dependency; with `--gradle`, assembles both modules and `:app` |
| `G1` | data imports vs. allowlist, data visibility (D-10: only the manager interface, `model/` classes and the `<Feature>Module` object are public), test imports, test visibility (TR-6: everything `internal` except JUnit classes/abstract bases), `@see openspec … task` KDoc, data scenario coverage (`Scn:`), test weakening since `--base`; with `--gradle`, `:<feature>:data:testDebugUnitTest` |
| `G2` | presentation imports and forbidden imports, presentation visibility VIS-1 (every top-level declaration `internal`; destinations live in `:common:navigation`), presentation structure S-1 (fixed directories, role-suffixed names, one Contract/ViewModel, screens = entries = UiModels = `<Screen>ScreenNavDestination.kt` files in `common/navigation/.../destination/<feature>/`, no `model/destination/`; CN-1: those destinations are public `@Serializable` classes extending `INavigationDestination()` with a task KDoc and only core-navigation/serialization imports, `view/components/`), layering L-1 (manager-only data access, `IAnalyticsManager` only in `analytics/`), form placement (field states in the Contract, `FormState` inside `remember` in the view), analytics scaffold (`analytics/<Feature>Analytics.kt`, `events/`, `views/`, no `IAnalyticsManager` in ViewModels), forbidden symbols (hard-coded colours, `MaterialTheme.*`, `CurrencyType.Symbol`, `PagingComponent`/`ContainerComponent`), Checkbox/Toggle `onValueChange`, `Render` calls `loadInitData()`, task references; with `--gradle`, compile + `:app:assembleDebug` |
| `G3` | presentation test structure (S-2: one `<Screen>FeatureTest` per navigation entry, `<Feature>NavigationRegistrationTest`, `di/*ModuleTest` only), integration test imports, test visibility (TR-6), no `@UninstallModules`/`@BindValue` in presentation tests (TR-4), no API service/DTO/`NetworkModule`/`ManagerImpl` references there (TR-5), presentation + analytics scenario coverage, test weakening, `openspec validate --strict`; with `--gradle`, both modules' unit tests |

- Exit code 0 means no CRITICAL findings (`"verdict": "PASS"`); exit 1 means `"FAIL"`.
- Each finding has `rule`, `severity`, `location`, `evidence` and `owner`. Copy them unchanged into the eval verdict, then add `wiki` and `requirement`.
- Always pass `--gradle` at gates. Pass `--base <commit of the previous gate>` from G1 on so deleted or ignored tests are detected.
- Detekt is **not** part of this script because `configureDetekt` uses `autoCorrect = true` and rewrites files. Run it only in a throwaway worktree (proposal §9) and report findings as warnings.

## After running

Mechanical PASS is necessary but not sufficient: eval agents then apply the judged rules of §7.2 to the files that passed.
