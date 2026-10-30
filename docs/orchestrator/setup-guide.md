# Setup guide: wiki-driven, OpenSpec-orchestrated feature delivery

This guide explains how this repository was configured so that a team of Claude Code agents can deliver a whole feature
(modules, data layer, presentation layer, analytics, tests) under strict rules, and how to reproduce the same setup in any
application: Android, iOS or Flutter.

The Android files in this repo are the reference implementation. Every step names the file that implements it here and
explains what to change for other platforms. The full design rationale is in [`proposal.md`](proposal.md); the visual
overview is [`pipeline.html`](pipeline.html).

---

## 1. The idea in one page

Three sources of truth, each with one job:

| Source | Answers | Lives in | Who writes it |
|---|---|---|---|
| **Wiki** (OpenWiki) | HOW to build anything in this codebase: architecture, base classes, recipes, gotchas | `openwiki/` | Generated from source, refreshed on a schedule. Never hand-edited |
| **Spec** (OpenSpec) | WHAT to build for one feature: requirements, WHEN/THEN scenarios, design contracts, role-tagged tasks | `openspec/changes/<id>/` | A coordinator agent, approved by a human |
| **Orchestrator** | WHO does what, in which order, and what "done" means | `.claude/` + `docs/orchestrator/` + `scripts/orchestrator/` | You, once per project |

Delivery flow:

```text
feature request
  → coordinator writes an OpenSpec change ─ G0a (validate) ─ G0b (human approves plan)
  → Phase 0  module team    (module agent → module eval)                         ─ G0c
  → Phase 1  data team      (data agent ⇄ data test agent → data eval)          ─ G1
  → Phase 2  presentation   (viewmodel ∥ view → analytics → presentation eval)  ─ G2
  → Phase 3  testing team   (feature-integration tests, defects routed back)     ─ G3
  → human approves (G4) → openspec archive
```

Two mechanisms make "follow the wiki strictly" enforceable instead of a wish:

1. **Read-only zones** guarded by a Claude Code hook: agents can't edit core modules, the wiki, the accepted specs or the orchestrator itself.
2. **Eval agents** at every gate: a deterministic script runs first (build, imports against an allowlist derived from the wiki,
   scenario coverage), then a read-only reviewer applies a rubric in which every rule cites a wiki section.

Each team has the same shape: a **builder** agent that writes code, and a **read-only eval** agent that reviews it against the wiki for that layer.
Findings go back to the builder in a bounded loop (3 rounds). Anything beyond a limit stops and returns to a human.

---

## 2. Prerequisites

| Tool | Why | Check |
|---|---|---|
| Claude Code | Agents, skills, hooks, workflows | `claude --version` |
| OpenWiki (skill + MCP server) | Generates and serves the wiki | the `openwiki` skill and the `openwiki_*` MCP tools are available in a session |
| OpenSpec **≥ 1.15** | Spec-driven change lifecycle | `openspec --version` |
| Python 3 (standard library only) | Mechanical gate script | `python3 --version` |
| `jq` | Guard hook parses tool input | `jq --version` |
| Node (optional) | Syntax-checking the workflow script | `node --version` |
| Git | Diff-based checks, gate commits | — |
| Your platform build tool | Compile and test gates | Gradle / `xcodebuild` or `swift` / `flutter` |

Install OpenSpec with one of: `npm install -g @fission-ai/openspec@latest`, `brew install openspec`, or the install script in the OpenSpec README. Pin the version you
validated in your team README: command names and file layout change between versions.

---

## 3. Step 1: Define your layering

The orchestrator relies on a codebase that already has **core modules** (shared infrastructure written by humans) and a
**repeatable feature-module shape**. Write it down before anything else, because every later artifact refers to it.

| Concept | Android (this repo) | iOS | Flutter |
|---|---|---|---|
| Core data module | `:core:data` (network, cache, security, analytics, session) | `CoreData` Swift package | `packages/core_data` |
| Core presentation module | `:core:presentation` (base ViewModel, components, theme, navigation) | `CorePresentation` package (SwiftUI components, base view models, router) | `packages/core_presentation` (widgets, theme, base notifiers/blocs, router) |
| Build configuration | `core/dependencies` (convention plugins + `coreLibs` version catalog) | `Package.swift` manifests, or Tuist/XcodeGen project definitions | root `pubspec.yaml` workspace / melos config, `analysis_options.yaml` |
| Feature data module | `:<f>:data`, plugin `feature.data` | `<F>Data` package | `features/<f>/data` package |
| Feature presentation module | `:<f>:presentation`, plugin `feature.presentation` | `<F>Presentation` package | `features/<f>/presentation` package |
| App wiring | `app/build.gradle.kts` + `settings.gradle.kts` | app target dependencies (Xcode project / Tuist `Project.swift`) | app `pubspec.yaml` path dependencies |
| Dependency edges | `:app` → `:<f>:presentation` → `:<f>:data` → `:core:data` | App → `<F>Presentation` → `<F>Data` → `CoreData` | app → `<f>_presentation` → `<f>_data` → `core_data` |

Rules that carry over to every platform:

- A feature is **two modules** (data, presentation). The presentation module depends on the data module, and the app depends on the presentation module.
- Core modules and build configuration are **read-only** during feature work. If a feature needs a core change, the run stops and a human decides.
- Each feature module has a **unique identity**. On Android that's the `namespace`; on iOS the product/target name and bundle ID prefix;
  on Flutter the package name.
- Feature modules get dependencies **only through the build configuration you control** (convention plugins, a shared `Package.swift` helper,
  or a workspace-level dependency list). No new third-party dependency per feature.

> Before the first run, audit the base framework for gaps that would block any feature. This repo found two (ViewModels created by
> Navigation3 had no Hilt factory, and the feature plugins had no test setup for Hilt and Robolectric) and recorded them as prerequisites
> P1/P2 that the preflight check detects. Expect the same kind of gap elsewhere, for example a router that can't inject view models
> on iOS, or no test harness for the DI container in Flutter.

---

## 4. Step 2: Generate the wiki with OpenWiki

The wiki is the rulebook for every agent. It has to be **exhaustive**: an agent only follows rules it can read.

### 4.1 One wiki section per core module

Generate one section per core module, one at a time, with a prompt like the template below. This repo used three:
`openwiki/core-data/` (15 pages), `openwiki/core-presentation/` (16 pages) and `openwiki/core-dependencies/` (12 pages), plus `openwiki/quickstart.md`.

```text
Use OpenWiki to generate (or update) the wiki for the complete <module path> module.
It is mandatory that the wiki covers every package and every class of the module, with as much detail as possible.
Agents will use the wiki to build features, so detail is the key requirement.

Each page must include:
- Responsibility and ownership of the package; how features consume it.
- Type reference: every public type with its exact signatures, parameters, defaults and return types.
- Mechanisms and data flow, with a diagram where useful.
- DI wiring: which bindings exist, their scopes, and how a feature obtains them.
- "Gotchas and constraints": known bugs, unsafe defaults, missing pieces, and the mandatory workaround for each.
- Recipes: copy-paste steps for the common feature tasks (e.g. declare an API service, cache a value, add a screen).
- Testing: how to test code that uses this package, with the existing test base classes.
Add a quickstart page with a task-routing map ("to do X, read page Y") and a "Known pitfalls to check before building" list.
Afterwards, verify that every source file of the module is referenced by at least one page.
```

Platform notes:
- **iOS:** cover every public type of each package, including protocol requirements, actors and `@MainActor` isolation, `async` APIs and error
  types, SwiftUI environment values, and the router/coordinator API.
- **Flutter:** cover every exported library, including widget constructor parameters, state-management base classes (Bloc/Cubit/Notifier),
  `get_it`/Riverpod registrations, `go_router` routes, and the `Result`/`Either` types.
- Always cover **build configuration** as its own section (Gradle plugins, `Package.swift`, `pubspec.yaml`/melos). The module team is evaluated against it.

### 4.2 Keep it fresh and read-only

- Schedule regeneration. This repo uses `.github/workflows/openwiki-update.yml`; OpenWiki's claim validation keeps pages tied to source lines.
- Never hand-edit generated pages. Fix the source or its doc comments and let OpenWiki regenerate.
- Add a short "OpenWiki" section to `AGENTS.md`/`CLAUDE.md` telling agents to read the wiki only when needed (see this repo's `AGENTS.md`).
- After every regeneration, re-derive the allowlist (Step 5). The preflight check flags it as stale otherwise.

---

## 5. Step 3: Install and configure OpenSpec

```bash
openspec init --tools claude          # creates openspec/{specs,changes}, config.yaml, .claude/skills/openspec-*, .claude/commands/opsx/*
openspec --version                    # record it
ls .claude/commands/opsx .claude/skills
```

What you get with the default `core` profile (v1.15): `/opsx:propose`, `/opsx:explore`, `/opsx:apply`, `/opsx:update`, `/opsx:sync`, `/opsx:archive`,
and the matching `openspec-*` skills. `/opsx:verify` only exists in the expanded profile, so the gates in this setup don't depend on it.

The CLI commands the orchestrator uses (all have `--json`):

| Command | Used for |
|---|---|
| `openspec new change <id>` | Create the change folder |
| `openspec status --change <id> --json` | Which artifacts are done (`isPlanningComplete`) |
| `openspec instructions <artifact\|apply\|archive> --change <id> --json` | Templates, context and task lists for agents |
| `openspec validate <id> --strict --json` | Gate G0a and G3 |
| `openspec show <id> --json --deltas-only` | Requirements and scenarios for coverage checks and human review |
| `openspec archive <id> --yes` | Final merge of the deltas into `openspec/specs/` |

Before relying on anything, **probe it**: create a throw-away change, add the extra things you plan to put in it (a `.orchestrator/` run-state folder,
role-tagged task lines, custom `x-` keys in `.openspec.yaml`), run `openspec validate --strict`, then delete it.
In this repo the probe showed that `x-` keys must be **appended** (redeclaring `schema:` breaks `openspec status`).

### 5.1 `openspec/config.yaml`

This is the most important configuration file: its `context` is injected into every artifact and every apply instruction. Structure
(full example: [`openspec/config.yaml`](../../openspec/config.yaml)):

```yaml
schema: spec-driven
context: |
  <stack in one line: language, UI framework, DI, navigation, toolchain versions>
  HOW to build anything is defined by openwiki/ (start at openwiki/quickstart.md). <one line per wiki section>
  Features are delivered by the orchestrator in docs/orchestrator/proposal.md.
  Feature layout: <data module + identity>, <presentation module + identity, depends on data>; <app depends on presentation>.
  Only technology documented in the wiki may be used (docs/orchestrator/allowlist.yaml). No new dependencies.
  Read-only for feature work: <core paths>, openwiki/**, openspec/specs/**.
  Platform features that don't work yet must be declared BLOCKED in the proposal instead of being implemented.
rules:
  proposal: [sections Why / What Changes / Impact / Platform dependencies / Out of scope / Analytics, ...]
  specs:    [capabilities are exactly <f>-module, <f>-data, <f>-presentation, <f>-analytics; one SHALL per requirement; testable WHEN/THEN scenarios]
  design:   [sections Data API surface, Cache policy, Presentation contract, Navigation, Analytics map, Test plan; cite the wiki for every decision]
  tasks:    [sections 0. Module / 1. Data / 2. Presentation / 3. Testing; one role tag per line; "Req: <capability>/<Requirement>"; test tasks add "Scn: <Scenario>"]
operations:
  apply:   { guidance: [only your role's tasks; read your wiki pages first; add "@see openspec <id> task <n.m>" to new files; stop and file a spec-change request if blocked] }
  archive: { guidance: [archive only when .orchestrator/archive-approved exists] }
```

The **role tags** (`[module]`, `[data]`, `[data-test]`, `[viewmodel]`, `[view]`, `[analytics]`, `[testing]`) and the `Req:`/`Scn:` suffixes are what make
traceability checkable by a script: every task maps to a requirement, and every scenario maps to a test.

---

## 6. Step 4: Teams, roles and ownership

### 6.1 Roster

| Phase | Team | Builder(s) | Evaluator | Wiki section |
|---|---|---|---|---|
| Plan | Coordinator | `feature-coordinator` (writes the OpenSpec artifacts, never code) | script gate G0a + human G0b | quickstart + all recipes |
| 0 | Module | `feature-module` | `feature-module-eval` (G0c) | build configuration (`core-dependencies`) |
| 1 | Data | `feature-data`, `feature-data-test` | `feature-data-eval` (G1) | core data |
| 2 | Presentation | `feature-viewmodel` ∥ `feature-view`, then `feature-analytics` | `feature-presentation-eval` (G2) | core presentation (+ analytics) |
| 3 | Testing | `feature-testing` | script gate G3 + human G4 | testing pages of both core modules |

Platform names for the presentation builders: **Android** ViewModel + Compose screen; **iOS** `ObservableObject`/`@Observable` view model + SwiftUI view;
**Flutter** Bloc/Cubit/Notifier + widget. The split (state holder vs. UI, in parallel, with the contract fixed in `design.md`) is the same.

### 6.2 Agent definition files (`.claude/agents/<name>.md`)

Every agent file has the same sections. Use this repo's files as templates and replace the paths and wiki pages:

```markdown
---
name: feature-<role>
description: <one sentence: what it builds or evaluates, which tag/gate>
tools: Read, Grep, Glob, Write, Edit, Bash, Skill      # evals: no Edit; Write only for verdict files
model: sonnet                                         # opus for the coordinator and all evals
---
You implement only the `[<role>]` tasks. Use the `wiki-context` skill with role `<role>`, then `openspec instructions apply --change <id> --json`.

## Owned paths
<exact globs; nothing else may be written>

## What to create / Rules
<the shape to follow, copied from the wiki recipe; forbidden things; known-bug workarounds>

## Done
<the narrowest build/test command that proves it; the gate script command; tasks checked; what to return>
```

Eval agents add a **Procedure** (1. run the mechanical gate and stop on CRITICAL; 2. apply the judged rubric rules, opening the cited wiki section for each;
3. check traceability against the spec) and a **Verdict** JSON format with `rule`, `severity`, `wiki`, `requirement`, `location`, `evidence`, `owner`, `fix_hint`.
A finding without a wiki citation isn't a finding.

### 6.3 Ownership map

Write a table of path → owner (proposal §6.1). Builders in the same phase must own **disjoint** paths so they can run in parallel. Where two roles need
the same file (analytics calls inside the view model), give the second one a **sequential, insert-only lease** and let the eval check the diff shape.

---

## 7. Step 5: The allowlist (`docs/orchestrator/allowlist.yaml`)

The allowlist turns "only use what the wiki documents" into something a script can check. Derive it from the wiki pages (the `allowlist-derive` skill does
this) and stamp it with the wiki update date.

```yaml
source_wiki_update: "2026-10-26"
data:
  imports_allowed: [<core data packages>, <networking lib>, <serialization lib>, <DI annotations>, <coroutines/concurrency>]
  forbidden_imports: [<presentation packages>, <UI framework>]
presentation:
  imports_allowed: [<core presentation packages>, <core data public types>, <UI framework>, <navigation>, <DI>]
  forbidden_imports: [<legacy navigation package>, <networking lib>]
  forbidden_symbols:
    - pattern: '<regex of a documented bug or banned API>'
      rule: V-K1
tests:
  imports_allowed: [<test framework>, <mocking lib>, <coroutine test utilities>]
```

| Platform | What an "import" is | Example entries |
|---|---|---|
| Android | `import com.x.y.Z` | `com.core.data.network.`, `retrofit2.`, `dagger.hilt.` |
| iOS | `import Module` (plus `@testable import`) | `CoreData`, `SwiftUI`, `Observation`, `Factory` |
| Flutter | `import 'package:x/...'` | `package:core_data/`, `package:flutter/`, `package:go_router/` |

---

## 8. Step 6: Gates and rubric

### 8.1 Gates

| Gate | After | Exit criteria |
|---|---|---|
| preflight | — | OpenSpec version, base-framework prerequisites present, allowlist not older than the wiki |
| G0a | planning | `openspec validate --strict`; every task has a role tag and `Req:`; every scenario has a test task |
| G0b | G0a | human approves the plan (marker file `.orchestrator/plan-approved`) |
| G0c | Phase 0 | modules build; module rubric (M-*) passes in the module eval |
| G1 | Phase 1 | data unit tests green; every data scenario has a test; data-eval verdict PASS |
| G2 | Phase 2 | presentation compiles, the app builds; presentation-eval verdict PASS |
| G3 | Phase 3 | all feature tests green; scenario coverage; `validate --strict` still green |
| G4 | G3 | human approves archive (marker file `.orchestrator/archive-approved`) |

### 8.2 Rubric

Rule IDs are `<layer>-<n>`, each with a check type (**M**echanical or **J**udged) and a wiki citation. Families used here: `M-*` module, `D-*` data,
`D-T*` data tests, `VM-*` view model, `V-*` view, `V-N*` navigation, `V-K*` known core bugs, `AN-*` analytics, `TR-*` traceability (full list: proposal §7.2).

How to write the rules for a new codebase:
1. For each wiki page, turn every **recommendation** into a rule ("API services return `ApiResult<T>` and are created with `RetrofitFactory`").
2. Turn every **gotcha** into a rule with its workaround ("compare `NoContent` with `is`", "Checkbox needs an explicit `onValueChange`").
3. Make a rule **mechanical** whenever a grep, import scan or build can decide it; leave architecture conformance to the judged pass.
4. Keep the module rules about the build configuration only: plugin/manifest shape, unique identity, exact dependency edges, append-only edits to shared files,
   no new external dependencies, gotchas reported rather than worked around.

---

## 9. Step 7: The mechanical gate script

[`scripts/orchestrator/eval-mechanical.py`](../../scripts/orchestrator/eval-mechanical.py) uses only the Python standard library:

```text
python3 -I scripts/orchestrator/eval-mechanical.py --change <id> --feature <f> --gate <preflight|G0a|G0c|G1|G2|G3> [--gradle] [--base <git-ref>]
```

It prints a verdict JSON (`verdict`, `findings[]` with `rule`, `severity`, `location`, `evidence`, `owner`) and exits 1 on any CRITICAL. Port it by replacing
four platform hooks:

| Function | Android | iOS | Flutter |
|---|---|---|---|
| Source file scan | `*.kt` under `src/main`, `src/test` | `*.swift` under `Sources/`, `Tests/` | `*.dart` under `lib/`, `test/` |
| Import regex | `^import\s+([\w.]+)` | `^(@testable\s+)?import\s+(\w+)` | `^import\s+'(package:[^']+)'` |
| Module checks (G0c) | `build.gradle.kts` applies the plugin, `namespace`, `project(":<f>:data")`, `settings.gradle.kts` include, `:app` dependency | `Package.swift` products/targets, dependency on `<F>Data`, app target dependency | `pubspec.yaml` name, `path:` dependency on `<f>_data`, workspace member, app dependency |
| Build/test commands | `./gradlew :<f>:data:testDebugUnitTest`, `:app:assembleDebug` | `swift build`, `swift test --package-path <F>Data`, `xcodebuild -scheme App build` | `flutter analyze`, `flutter test features/<f>/data`, `flutter build apk --debug` (or `ios --no-codesign`) |

Platform-independent parts to keep: OpenSpec validation, scenario-to-test coverage (scan test files for `Scn: <scenario>` doc comments), task `Req:` checks,
test-weakening detection against `--base` (deleted tests, removed assertions), and the allowlist staleness check.

> Formatters and linters that **rewrite files** (Detekt with `autoCorrect` here; `swiftformat`, `dart fix --apply`, `dart format` elsewhere) must run in a
> throw-away worktree during a gate, keeping only their report. Otherwise the gate silently changes the code it's judging.

Dry-run the script against an existing module (here `demo/*`) and confirm it reports exactly the issues you already know about.

---

## 10. Step 8: Shared skills (`.claude/skills/`)

| Skill | What it does | Port effort |
|---|---|---|
| `wiki-context` | Maps each role to the wiki pages it must read in full, then extracts the gotchas, exact signatures and that role's rubric rules | Rewrite the role → pages table |
| `eval-mechanical` | Documents how to run the gate script and read its verdict | Change the command examples |
| `allowlist-derive` | Regenerates the allowlist from the wiki and stamps the date | Change the import syntax |

Skills keep agent prompts short: every agent starts with "use `wiki-context` with role X" instead of embedding page lists.

---

## 11. Step 9: Guardrail hook

[`scripts/orchestrator/guard-paths.sh`](../../scripts/orchestrator/guard-paths.sh) is a `PreToolUse` hook. It's active only while
`.claude/orchestrator-active` exists (the workflow creates it at the start of `implement` and removes it after archive), so normal development is unaffected.
It blocks (exit 2, message to the agent):
- edits to core modules, the wiki, `openspec/specs/`, `openspec/config.yaml`, root build files, the orchestrator's own files and demo code;
- `openspec archive` without `.orchestrator/archive-approved`;
- `git push`;
- shell writes (`sed -i`, redirections, `tee`) into read-only zones.

Register it in `.claude/settings.json`:

```json
{
  "hooks": {
    "PreToolUse": [{
      "matcher": "Edit|Write|MultiEdit|NotebookEdit|Bash",
      "hooks": [{ "type": "command", "command": "\"$CLAUDE_PROJECT_DIR\"/scripts/orchestrator/guard-paths.sh", "statusMessage": "Orchestrator guard" }]
    }]
  }
}
```

Port it by editing the `case "$rel" in` list: iOS `Packages/Core*/*`, `*.xcodeproj/*`, `Tuist/*`, `Package.resolved`; Flutter `packages/core_*/*`,
the root `pubspec.yaml`, `pubspec.lock`, `melos.yaml`, `analysis_options.yaml`.

Prove it works: create the marker, try a blocked command (for example `openspec archive zz-probe --yes`), confirm the block message, remove the marker.

Limitation: hooks can't tell which subagent is running, so **per-role** path ownership is checked after the fact by the eval agents (diff against the ownership map);
the hook only enforces the global read-only zones.

---

## 12. Step 10: The workflow script

[`.claude/workflows/openspec-feature.js`](../../.claude/workflows/openspec-feature.js) drives everything deterministically. It takes:

```json
{ "feature": "orders", "stage": "plan | implement | archive", "request": "<natural-language feature request>",
  "allowMissingPrerequisites": false, "baseRef": "<git sha before implement>" }
```

Structure (platform-independent; only the prompts mention platform names):
1. **Preflight**: run the mechanical `preflight` gate; stop unless only known prerequisites are missing and `allowMissingPrerequisites` is set.
2. **`plan` stage**: coordinator writes the change, G0a runs, the workflow returns "awaiting plan approval".
3. **`implement` stage**: check `plan-approved`, write `.claude/orchestrator-active`, then for each phase run the builder(s), then the
   `eval → route findings by owner → fix` loop (≤ 3 rounds). The data phase has an inner `test ⇄ fix` loop (≤ 8 fixes) where disputed test failures are ruled
   on by the data eval. ViewModel and view run in `parallel()`. Testing routes defects to the owning layer (≤ 3 rounds). Returns "awaiting archive approval".
4. **`archive` stage**: check `archive-approved`, `openspec archive <id> --yes`, remove the marker.

Every limit breach and every `spec`-attributed failure calls `stopForHuman(...)`: progress is never lost because checked boxes in `tasks.md` and the verdict files
are the durable state.

Check the syntax after edits by compiling the body as an async function with Node:

```bash
sed 's/^export const meta/const meta/' .claude/workflows/openspec-feature.js > /tmp/wf.js && node -e "new (Object.getPrototypeOf(async function(){}).constructor)('args','agent','parallel','phase','log', require('fs').readFileSync('/tmp/wf.js','utf8')); console.log('syntax ok')"
```

---

## 13. Step 11: Run the pilot

```text
Workflow openspec-feature  args: {"feature": "orders", "stage": "plan", "request": "<request>"}
# review: openspec show add-orders-feature   →   touch openspec/changes/add-orders-feature/.orchestrator/plan-approved
Workflow openspec-feature  args: {"feature": "orders", "stage": "implement", "baseRef": "<sha>"}
# review the feature                          →   touch openspec/changes/add-orders-feature/.orchestrator/archive-approved
Workflow openspec-feature  args: {"feature": "orders", "stage": "archive"}
```

Interactive alternative without the workflow: `/opsx:propose` (plan), then invoke each `feature-*` agent in phase order, running the gate script between phases,
then `/opsx:archive`.

Pick a small pilot that avoids known gaps (no forms, paging or platform permissions). Record per phase: first-try gate pass rate, loop iterations, findings by rule ID,
spec-change requests, tokens and wall time. Tighten the rules that produced false positives and turn frequent judged findings into mechanical checks.

---

## 14. Maintenance loop

```text
source changes → OpenWiki regenerates (scheduled) → allowlist-derive → preflight no longer reports PRE-AL
core API changes → update the rubric rules that cite the changed pages → re-run the pilot feature in a branch
OpenSpec upgrade → re-run the probe change (Step 3) → update pinned version and the CLI calls if needed
```

---

## 15. Lessons learned (apply them from day one)

- **Wiki first, orchestrator second.** Every rule, allowlist entry and eval citation points into the wiki. A thin wiki produces vague evals.
- **Probe every tool assumption** (OpenSpec folder tolerance, metadata keys, installed commands) before encoding it into agents.
- **Approvals as marker files** are simple, scriptable and checked by both the workflow and the hook.
- **Base-framework gaps are prerequisites, not agent tasks.** Detect them in preflight; agents must never patch core code to unblock themselves.
- **Builder + read-only evaluator per layer**, including the module/build layer. A compile check alone misses wrong dependency edges, `api` vs. `implementation`
  leaks, edits to shared files and silent workarounds.
- **Mechanical before judged:** it's cheaper, deterministic, and the judged pass only reviews code that already builds.
- **Never let a fix loop weaken tests:** production code is fixed by default; a test changes only when the evaluator rules it wrong; a spec problem stops the run.
- **Creating agent files with Bash/Write tools** may need explicit human confirmation in Claude Code's auto mode. Plan for that approval step when you
  bootstrap the setup.

---

## 16. Bootstrap checklist

- [ ] Layering written down: core modules, feature module shape, dependency edges, read-only zones (Step 1)
- [ ] Base-framework prerequisites audited and either fixed or detectable by preflight
- [ ] Wiki generated per core module + quickstart, coverage verified, scheduled refresh in place (Step 2)
- [ ] OpenSpec installed, version pinned, probe change passed, `openspec/config.yaml` written (Step 3)
- [ ] Agent files for coordinator, module + module eval, data + data test + data eval, viewmodel + view + analytics + presentation eval, testing (Step 4)
- [ ] Ownership map with disjoint paths and leases
- [ ] Allowlist derived and stamped (Step 5)
- [ ] Gates and rubric written with wiki citations (Step 6)
- [ ] Mechanical gate script ported and dry-run against an existing module (Step 7)
- [ ] Skills `wiki-context`, `eval-mechanical`, `allowlist-derive` (Step 8)
- [ ] Guard hook registered and proven (Step 9)
- [ ] Workflow script syntax-checked (Step 10)
- [ ] Pilot feature run end to end, metrics recorded (Step 11)

## Reference files in this repository

| File | Role |
|---|---|
| `openwiki/` | Generated wiki (core-data, core-presentation, core-dependencies, quickstart) |
| `.github/workflows/openwiki-update.yml` | Scheduled wiki refresh |
| `openspec/config.yaml` | OpenSpec context, artifact rules, apply/archive guidance |
| `docs/orchestrator/proposal.md` | Full design: roles, gates, rubric, ownership, fix loops, templates |
| `docs/orchestrator/allowlist.yaml` | Import allowlist derived from the wiki |
| `docs/orchestrator/pipeline.html` | Visual overview of the pipeline |
| `scripts/orchestrator/eval-mechanical.py` | Mechanical gate checks |
| `scripts/orchestrator/guard-paths.sh` + `.claude/settings.json` | Guardrail hook |
| `.claude/agents/feature-*.md` | Role definitions |
| `.claude/skills/{wiki-context,eval-mechanical,allowlist-derive}/` | Shared routines |
| `.claude/workflows/openspec-feature.js` | Orchestration workflow |
