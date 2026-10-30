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
| `G0c` | module build files (plugin, namespace, no external deps, presentation → data), `settings.gradle.kts` includes, `:app` dependency; with `--gradle`, assembles both modules and `:app` |
| `G1` | data imports vs. allowlist, test imports, `@see openspec … task` KDoc, data scenario coverage (`Scn:`), test weakening since `--base`; with `--gradle`, `:<feature>:data:testDebugUnitTest` |
| `G2` | presentation imports and forbidden imports, forbidden symbols (hard-coded colours, `MaterialTheme.*`, `CurrencyType.Symbol`, `PagingComponent`/`ContainerComponent`), Checkbox/Toggle `onValueChange`, `Render` calls `loadInitData()`, task references; with `--gradle`, compile + `:app:assembleDebug` |
| `G3` | integration test imports, presentation + analytics scenario coverage, test weakening, `openspec validate --strict`; with `--gradle`, both modules' unit tests |

- Exit code 0 means no CRITICAL findings (`"verdict": "PASS"`); exit 1 means `"FAIL"`.
- Each finding has `rule`, `severity`, `location`, `evidence` and `owner`. Copy them unchanged into the eval verdict, then add `wiki` and `requirement`.
- Always pass `--gradle` at gates. Pass `--base <commit of the previous gate>` from G1 on so deleted or ignored tests are detected.
- Detekt is **not** part of this script because `configureDetekt` uses `autoCorrect = true` and rewrites files. Run it only in a throwaway worktree (proposal §9) and report findings as warnings.

## After running

Mechanical PASS is necessary but not sufficient: eval agents then apply the judged rules of §7.2 to the files that passed.
