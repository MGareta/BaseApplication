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
- **Insert-only lease** on `.../viewModel/*ViewModel.kt`: you may add the `<Feature>Analytics` constructor parameter, its import, and calls to its `track*` methods. You may not delete or
  rewrite any other line (rubric AN-4).

## Scaffold (rubric AN-5)
```
analytics/
├── <Feature>Analytics.kt     # the only analytics type a ViewModel knows
├── events/<Name>Event.kt     # one file per event
└── views/<Name>View.kt       # one file per screen view
```
Packages are `com.<feature>.presentation.analytics`, `.analytics.events` and `.analytics.views`. `<Feature>` is the PascalCase feature name (`login` → `LoginAnalytics`).

### `<Feature>Analytics`
A plain injectable **class** (not an `object`: it needs the Hilt-provided `IAnalyticsManager`, and presentation tests replace that binding with a `@TestInstallIn` module under `src/test/.../di/`). No Hilt module is needed.
```kotlin
internal class LoginAnalytics @Inject constructor(
    private val analyticsManager: IAnalyticsManager,
) {
    fun trackLoginView() = analyticsManager.trackView(framework = AnalyticsFramework.FIREBASE, view = LoginView())
    fun trackLoginSucceeded() = analyticsManager.trackEvent(framework = AnalyticsFramework.FIREBASE, event = LoginSucceededEvent())
    fun trackLoginFailed(reason: String) = analyticsManager.trackEvent(framework = AnalyticsFramework.FIREBASE, event = LoginFailedEvent(reason))
}
```
- Exactly one `track<Name>` function per *Analytics map* entry, in the map's order. It takes plain values (String, Int, enums…), never event/view instances, and returns `Unit`.
- It is the only place that touches `IAnalyticsManager`, `AnalyticsFramework` and the event/view classes.
- No logic beyond building the event/view and calling the manager (no state, no branching), so the ViewModel stays a one-line call per hook.

## Rules (`openwiki/core-data/analytics.md`)
- **Visibility (rubric VIS-1): `internal` whenever possible.** `<Feature>Analytics`, every event and every view class is `internal`.
- Views (`analytics/views/`): `internal class <Name>View : BaseFirebaseTrackView(data = mapOf("screen_name" to "<screen>"))`. Firebase constants aren't visible to features, so use the
  literal key `"screen_name"`.
- Events (`analytics/events/`): `internal class <Name>Event(<params>) : BaseFirebaseTrackEvent(key = "<event_key>", data = mapOf("<param>" to value))`. Values must be `bundleOf`-compatible (String, Int, Long, Double, Boolean).
- Always pair `AnalyticsFramework.FIREBASE` with `BaseFirebaseTrack*` types, inside `<Feature>Analytics` only.
- ViewModel hooks (from the *Analytics map*): inject `private val analytics: <Feature>Analytics` and call `analytics.track<Name>(…)` at each hook, for example inside `onSuccess` of
  `loadInitData` or in the `handleUiEvents` branch. Never inject `IAnalyticsManager` into a ViewModel and never import `AnalyticsFramework` or an event/view class there (AN-6).
- Never call `analyticsManager.initialize()`: that's app-level and out of scope.
- Add `@see openspec <change-id> task <n.m>` to each new file's KDoc.

## Done
`./gradlew :<feature>:presentation:compileDebugKotlin` succeeds, every map entry has an event/view class, one `<Feature>Analytics` method and exactly one call site in a ViewModel, and your tasks are checked.
