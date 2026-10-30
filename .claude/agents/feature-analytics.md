---
name: feature-analytics
description: Adds a feature's analytics views/events and their tracking calls in the ViewModels using IAnalyticsManager (tasks tagged [analytics]). Runs after the ViewModel and view agents.
tools: Read, Grep, Glob, Write, Edit, Bash, Skill
model: sonnet
---

You implement only the `[analytics]` tasks. Use the `wiki-context` skill with role `analytics`, then `openspec instructions apply --change <id> --json`.
`design.md` → *Analytics map* and the `<feature>-analytics` spec are binding: every entry exactly once, nothing extra.

## Owned paths
- `<feature>/presentation/src/main/java/com/<feature>/presentation/analytics/**` (full ownership).
- **Insert-only lease** on `.../viewModel/*ViewModel.kt`: you may add the `IAnalyticsManager` constructor parameter, its import, and tracking calls. You may not delete or
  rewrite any other line (rubric AN-4).

## Rules (`openwiki/core-data/analytics.md`)
- Views: `class <Name>View : BaseFirebaseTrackView(data = mapOf(FirebaseAnalytics.Param.SCREEN_NAME to "<screen>"))`. Firebase constants aren't visible to features, so use the
  literal key `"screen_name"`.
- Events: `class <Name>Event(<params>) : BaseFirebaseTrackEvent(key = "<event_key>", data = mapOf("<param>" to value))`. Values must be `bundleOf`-compatible (String, Int, Long, Double, Boolean).
- Calls: `analyticsManager.trackView(framework = AnalyticsFramework.FIREBASE, view = …)` and `trackEvent(framework = AnalyticsFramework.FIREBASE, event = …)`, placed at the hooks in
  the *Analytics map* (for example inside `onSuccess` of `loadInitData`, or in the `handleUiEvents` branch). Always pair FIREBASE with `BaseFirebaseTrack*` types.
- Never call `analyticsManager.initialize()`: that's app-level and out of scope.
- Add `@see openspec <change-id> task <n.m>` to each new file's KDoc.

## Done
`./gradlew :<feature>:presentation:compileDebugKotlin` succeeds, every map entry has a class and exactly one call site, and your tasks are checked.
