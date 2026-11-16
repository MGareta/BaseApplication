---
name: feature-data-test
description: Writes and runs scenario-mapped unit tests for a feature data module (tasks tagged [data-test]) following the core-data testing wiki, and attributes failures (code, test or spec).
tools: Read, Grep, Glob, Write, Edit, Bash, Skill
model: sonnet
---

You implement only the `[data-test]` tasks. Use the `wiki-context` skill with role `data-test`, then `openspec instructions apply --change <id> --json`.

## Owned paths
`<feature>/data/src/test/**`.

## How to test (`openwiki/core-data/testing.md`)
Base tests on the shared `core` test fixtures and this feature's OpenSpec scenarios. Do not use another feature's source or tests as a baseline; the current feature's approved design and scenarios determine collaborators and assertions.

- One or more tests per scenario of the `<feature>-data` capability. Name each test after the scenario and add KDoc `Scn: <feature>-data/<Scenario title>`.
- **When prerequisite P2 is in place** (feature plugins call `configureTests()` and `kspTest(hilt-compiler)`): follow the BaseTest pattern
  (Robolectric + `HiltTestApplication` + `HiltAndroidRule`, `UnconfinedTestDispatcher`, `test { }`). If `BaseTest` isn't shared via test fixtures, copy it into
  `<feature>/data/src/test/java/com/<feature>/data/BaseTest.kt` unchanged in behavior. Replace module-provided API services with a test-source
  `@TestInstallIn` module (for example, a fake API service replacing `<Feature>NetworkModule`) and inject the service into the Hilt test with
  `@Inject lateinit var` (write `@Inject internal lateinit var` when the type is `internal`, such as the API service; a test module replacing an internal data module is itself an `internal object`). If the approved Data API surface marks the API as `N/A`, do not add an API fake or replacement module; test the applicable manager behavior using the designed collaborators.
  Use `@BindValue` for other per-test collaborators when a replacement module is not appropriate.
- **When P2 is missing** (the coordinator tells you; `eval-mechanical --gate preflight` reports PRE-P2): use the documented "construct the impl directly" pattern
  (`core-data/testing.md` → *Replacing dependencies*). Instantiate `<Feature>ManagerImpl` with MockK fakes and real pure-Kotlin collaborators, and use
  `kotlinx.coroutines.test.runTest`. No `@HiltAndroidTest`, no Robolectric.
- **Visibility in tests: `internal` whenever possible.** Every `@TestInstallIn` module is an `internal object` (even when it only touches public types), and any helper, fake or fixture class/function you add in test sources is `internal`. Exceptions: the JUnit test class itself stays public (JUnit requires it), as do `abstract` base classes shared through test fixtures and test methods; use `internal` only on members whose type is `internal` (e.g. `@Inject internal lateinit var api`).
- Only imports allowed by `docs/orchestrator/allowlist.yaml` → `data` + `tests`. No MockWebServer, Truth or Mockito.

## Running and reporting
Run `./gradlew :<feature>:data:testDebugUnitTest`. For every failure, return
`{"test": "...", "scenario": "...", "failure": "<full message and relevant stack>", "attribution": "code|test|spec", "reason": "..."}`.
Attribution defaults to **code**. Choose **test** only if the test contradicts the spec or wiki, and **spec** if the scenario itself is ambiguous or wrong.

## Never
Weaken, `@Ignore` or delete a test to make it pass. Change a test only after the data eval rules `attribution = test`. Never edit production code.
