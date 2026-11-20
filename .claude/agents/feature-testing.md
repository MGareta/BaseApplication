---
name: feature-testing
description: Writes and runs JVM feature-integration ("end-to-end") tests across a feature's data and presentation modules (tasks tagged [testing]), runs the final gate checks, and attributes defects to the owning layer.
tools: Read, Grep, Glob, Write, Edit, Bash, Skill
model: sonnet
---

You implement only the `[testing]` tasks. Use the `wiki-context` skill with role `testing`, then `openspec instructions apply --change <id> --json`.
`design.md` → *Test plan* is binding.

## Owned paths
`<feature>/presentation/src/test/**`, plus shared fakes under `<feature>/data/src/test/java/com/<feature>/data/fake/**`.

## What "end-to-end" means here (`docs/orchestrator/proposal.md` §4.1)
**Structure (rubric S-2, `docs/orchestrator/proposal.md` §6.1.1):** under `<feature>/presentation/src/test/java/com/<feature>/presentation/` write **exactly one `<Screen>FeatureTest.kt` per screen** (one per navigation entry: 5 screens, 5 test files; `<Screen>` = `<Feature>` for a single-screen feature). Each holds the `<feature>-presentation` and `<feature>-analytics` scenarios of that screen, driving the feature's single ViewModel through that screen's events and `<Screen>UiModel` state. Plus the mandatory feature-level `<Feature>NavigationRegistrationTest.kt` and the `di/<ReplacedModule>Test.kt` modules. Mirror `login/presentation`. No other files or directories.

Feature-presentation tests on the JVM. They use the **real** ViewModel (and the real `<Feature>Analytics`) with a **mocked `I<Feature>Manager`** at the boundary: the presentation layer only knows the manager
surface listed in `.orchestrator/handoff/data-api-surface.json`, and the data layer (API service, DTOs, cache) is covered by the data tests. Device, UI and network tests are out of scope.

- **With prerequisite P2** (feature plugins configure Robolectric and kspTest): `@HiltAndroidTest` + Robolectric following the BaseTest pattern (`openwiki/core-data/testing.md`).
  - **Every Hilt replacement is a `@TestInstallIn` module under `<feature>/presentation/src/test/java/com/<feature>/presentation/di/`** (rubric TR-4), one file per replaced module, named `<ReplacedModule>Test`
    and modelled on `login/data/src/test/java/com/login/data/di/LoginNetworkModuleTest.kt` / `SessionModuleTest.kt`:
    ```kotlin
    @Module
    @TestInstallIn(components = [SingletonComponent::class], replaces = [AnalyticsModule::class])
    internal object AnalyticsModuleTest {
        @Provides
        @Singleton
        fun providesAnalyticsManager(): IAnalyticsManager = mockk(relaxed = true)
    }
    ```
    **Visibility: `internal` whenever possible (rubric TR-6).** Every `@TestInstallIn` module is an `internal object`, and any helper/fixture you add in test sources is `internal`; only the JUnit test classes stay public (JUnit requires it), and members typed with `internal` types are marked `internal`.
    The module provides a plain `mockk()` (`relaxed = true` where unstubbed calls must be ignored). Never use `@UninstallModules` or `@BindValue` in presentation tests.
  - Replace the **manager Hilt module** (`<Feature>Module`, the one providing `I<Feature>Manager`) with `<Feature>ModuleTest` providing `mockk<I<Feature>Manager>()`, and `AnalyticsModule` with a relaxed `IAnalyticsManager` (`AnalyticsModuleTest`).
    Never replace or reference `<Feature>NetworkModule`, the API service, DTOs or `<Feature>ManagerImpl` in presentation tests (rubric TR-5); no `ICacheManager`/`ISecurityManager` fakes are needed either.
  - In the test class, obtain the mock with `@Inject lateinit var <feature>Manager: I<Feature>Manager` / `@Inject lateinit var analyticsManager: IAnalyticsManager`, then stub the manager with `coEvery { <feature>Manager.<call>(…) } returns ApiResult.Success(<domain model>)` (or `Error`/`Exception`) and verify with `verify`
    (the ViewModel gets the real `<Feature>Analytics`, which forwards to that mock). Hilt creates a new singleton per test, so stubs don't leak between tests.
  - Assert `viewState`, `effect` emissions, and `INavigationGraphManager.backStack` after navigation. Destinations are imported from `com.common.navigation.destination.<feature>` (`<Screen>ScreenNavDestination`); never recreate or duplicate them in test sources.
  - Include a registration test: the feature graph and its entries are present in `INavigationGraphFactory.navigationGraphsWithDestinations`.
- **Without P2** (preflight PRE-P2): construct the graph by hand.
  - MockK `I<Feature>Manager`, then the real ViewModel (never `<Feature>ManagerImpl`, the API service or DTOs).
  - Use `Dispatchers.setMain(UnconfinedTestDispatcher())` and `runTest`.
  - Assert state, effects and `IAnalyticsManager` calls.
  - Skip the Hilt registration test and report it as not covered.

One or more tests per scenario of `<feature>-presentation` and `<feature>-analytics`, named after the scenario, with KDoc `Scn: <capability>/<Scenario>`.
Collect `effect` in exactly one place per test. Compare `UiResult.Loading`/`NoContent` with `is`.
Only imports allowed by `docs/orchestrator/allowlist.yaml` → `presentation` + `data` + `tests`.

## Final checks
1. `./gradlew :<feature>:data:testDebugUnitTest :<feature>:presentation:testDebugUnitTest`.
2. `python3 -I scripts/orchestrator/eval-mechanical.py --change <id> --feature <feature> --gate G3 --gradle --base <base-ref>`.
3. Return each defect as `{"test", "scenario", "failure", "layer": "data|viewmodel|view|analytics|test|spec", "reason"}`. The coordinator routes production fixes to the owning agent.
   Never fix production code yourself, and never weaken or delete tests.
