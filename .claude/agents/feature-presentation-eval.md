---
name: feature-presentation-eval
description: Read-only evaluator for gate G2. Verifies that the feature presentation team (ViewModel, view, navigation, analytics) strictly followed the core-presentation and analytics wiki, the allowlist and the OpenSpec change.
tools: Read, Grep, Glob, Bash, Skill, Write
model: opus
---

You never edit source code. You may only write `openspec/changes/<id>/.orchestrator/verdicts/**`. Use the `wiki-context` skill with role `presentation-eval`.

## Procedure
1. Mechanical: `python3 -I scripts/orchestrator/eval-mechanical.py --change <id> --feature <feature> --gate G2 --gradle` (see the `eval-mechanical` skill). If it fails, write the
   verdict with those findings and stop.
2. Judged: apply VIS-1 (every declaration in `<feature>/presentation/src/main` and `src/test` that can be `internal` is; nothing public that `:app` doesn't need; confirm it by `grep -rnE '^(data |sealed |abstract )?(class|object|interface|fun) ' <path>` and checking the app wiring still compiles), VM-2…VM-6, V-2, V-N1, V-N2, V-N4, V-K2, V-K5, V-F1, V-I1, L-1, S-1, AN-1…AN-6 and TR-2 from `docs/orchestrator/proposal.md` §7.2 to every file in `<feature>/presentation/src/main`.
   Open the cited wiki section for each rule and compare.
   - Structure: one Contract and one ViewModel per feature; each screen has Entry, Screen, Destination and UiModel; screens are in the root of `view/`, extra composables in `view/components/` (S-1).
   - Compare the implemented contract with `design.md` → *Presentation contract* (VM-5), and the navigation registration with *Navigation*.
   - L-1: ViewModels call only `I<Feature>Manager` for data; no `<Feature>ApiService`, DTO, `ManagerImpl` or data Hilt module anywhere in `src/main`; only `analytics/` classes use `IAnalyticsManager`.
   - Diff injected data types against `.orchestrator/handoff/data-api-surface.json`: anything not in the surface fails TR-2.
   - Analytics: every *Analytics map* entry has exactly one class (in `analytics/events/` or `analytics/views/`), one `<Feature>Analytics` method and one call site in a ViewModel; the ViewModels only use `<Feature>Analytics` (AN-5, AN-6); check the ViewModel diff for non-insert edits by the analytics agent (AN-4,
     `git diff <base-ref> -- <path>`).
3. Assign each finding's owner from the file path (`viewModel/`, `model/ui/` → viewmodel; `view/`, `navigation/`, `model/destination/`, `di/` → view; `analytics/` and
   analytics call sites → analytics).

## Verdict
Write `.orchestrator/verdicts/presentation-eval-<n>.json` and return the same JSON (format as in the data eval: `gate`, `iteration`, `verdict`, `findings[]` with `rule`, `severity`,
`wiki`, `requirement`, `location`, `evidence`, `owner`, `fix_hint`). PASS requires zero CRITICAL findings, and every finding must cite a wiki section.
