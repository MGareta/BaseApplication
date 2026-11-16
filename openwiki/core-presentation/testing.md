---
type: testing guide
title: Testing core/presentation
description: The core/presentation JVM test setup, including its shared Robolectric/Hilt BaseTest fixture, dependencies exposed to feature-presentation tests, and unconfigured legacy androidTest classes. Includes templates for BaseViewModel, form-state, and Compose UI tests.
tags: [core-presentation, testing, robolectric, compose-test, hilt, coroutines]
sources:
  - id: openwiki-source-b93e89ddcc6bb7ffc877b9e1
    resource: repo://core/dependencies/src/main/java/com/core/dependencies/plugins/CorePresentationPlugin.kt
  - id: openwiki-source-1f78a6fb10e2b39879415b1c
    resource: repo://core/dependencies/src/main/java/com/core/dependencies/plugins/FeaturePresentationPlugin.kt
  - id: openwiki-source-cad10bb1596f0f842c7efb0a
    resource: repo://core/presentation/build.gradle.kts
  - id: openwiki-source-423851df6fa5276438a79cd2
    resource: repo://core/presentation/src/androidTest/java/com/core/presentation/BaseTest.kt
  - id: openwiki-source-f5bda66bca2d92811ddac55d
    resource: repo://core/presentation/src/androidTest/java/com/core/presentation/DefaultTestRunner.kt
generated: { by: "claude-code", at: "2026-11-02T14:42:55.956Z" }
verified:
  - by: openwiki/0.7.2
    at: 2026-11-02T14:42:55.956Z
---

# Testing core/presentation

## Current state

| Source set | Contents | Status |
|---|---|---|
| `src/test` | none | No core/presentation-owned JVM unit tests currently exist. |
| `src/testFixtures/java/com/core/presentation/BaseTest.kt` | Shared Robolectric/Hilt base class | Published for reuse by feature-presentation tests. |
| `src/androidTest/java/com/core/presentation/BaseTest.kt` | Legacy instrumented base class | Still present, but no `androidTest*` dependencies or `testInstrumentationRunner` are configured. |
| `src/androidTest/java/com/core/presentation/DefaultTestRunner.kt` | Legacy Hilt `AndroidJUnitRunner` | Still present, but not configured as the instrumentation runner. |

`CorePresentationPlugin` calls `configureTests()`, exposing Android resources and JDK module-open JVM arguments for Robolectric tests. It publishes the `test-unit` bundle
(kotlinx-coroutines-test, Robolectric, hilt-android-testing, mockk-android) and `:core:data` test fixtures through `testFixturesApi`, and adds `kspTest(hilt-compiler)`.
It does **not** add Compose UI test artifacts (`ui-test-junit4`/`ui-test-manifest`). `FeaturePresentationPlugin` adds `testImplementation(testFixtures(project(":core:presentation")))`,
so feature-presentation tests can reuse `com.core.presentation.BaseTest` and the test libraries. The core/presentation fixture also depends on the core/data fixtures.

### Shared `BaseTest` (testFixtures)
`com.core.presentation.BaseTest` runs with `RobolectricTestRunner` and `HiltTestApplication`, injects its Hilt rule and application context, initializes `ActivityProvider`,
and calls an abstract `initDependencies()` hook. Feature-presentation test classes can extend it and implement that hook for their test dependencies.

### Legacy `BaseTest` (androidTest)
```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseTest {
    @get:Rule abstract val hiltRule: HiltAndroidRule       // subclass provides HiltAndroidRule(this)
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testCoroutineScope = TestScope(testDispatcher + Job())
    @Before fun setup() { hiltRule.inject(); Dispatchers.setMain(testDispatcher) }
    @After fun cleanup() { Dispatchers.resetMain() }
    fun test(block: suspend TestScope.() -> Unit) = testCoroutineScope.runTest { block() }
}
```
It differs from core/data's `BaseTest`: there's no Robolectric runner or `@Config`, the rule is abstract, there's no `initDependencies()`, and `ActivityProvider` isn't initialized.

### `DefaultTestRunner` (androidTest)
`class DefaultTestRunner : AndroidJUnitRunner()` overrides `newApplication` to always instantiate `HiltTestApplication`. It would be referenced as
`testInstrumentationRunner = "com.core.presentation.DefaultTestRunner"`, but core/presentation's `build.gradle.kts` doesn't set it.

## Recommended approach: JVM tests in `src/test`

Follow core/data's pattern ([core-data testing](../core-data/testing.md)). For Compose tests, first add to the module (or to `CorePresentationPlugin`):
`testImplementation(platform(compose-bom))`, `testImplementation("androidx.compose.ui:ui-test-junit4")`, `debugImplementation("androidx.compose.ui:ui-test-manifest")`,
and `configureTests()` so Robolectric can load resources (fonts, strings, drawables).

### Template: BaseViewModel unit test (no Android needed)
```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class CatalogViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    @Before fun setUp() = Dispatchers.setMain(dispatcher)          // viewModelScope uses Dispatchers.Main.immediate
    @After fun tearDown() = Dispatchers.resetMain()

    @Test
    fun clickingProductEmitsOpenEffect() = runTest(dispatcher) {
        val vm = CatalogViewModel(getProducts = mockk(relaxed = true))
        val effects = mutableListOf<CatalogContract.Effect>()
        backgroundScope.launch { vm.effect.collect { effects += it } }

        vm.handleUiEvents(CatalogContract.Event.ProductClicked("42"))

        assertEquals(listOf(CatalogContract.Effect.OpenProduct("42")), effects)
    }

    @Test
    fun lazyResultMapsApiError() = runTest(dispatcher) {
        val vm = CatalogViewModel(getProducts = { ApiResult.Error(500, "boom") })
        val result = vm.products.first { it !is UiResult.Loading }     // withUseCaseScopeLazily
        assertEquals(UiResult.Error<List<ProductUi>>("boom"), result)
    }
}
```
Notes: `effect` is single-consumer, so collect it once. `UiResult.Loading`/`NoContent` aren't data classes, so assert them with `is`.

### Template: form state tests (pure JVM)
```kotlin
class SignUpFormTest {
    @Test fun requiredFirstAvoidsNpe() {
        val name = TextFieldState(null, { it.trim() }, listOf(Validators.Required("req"), Validators.MinSize(3) { "min" }))
        assertFalse(name.validate()); assertEquals("req", name.errorMessage)
        name.change("Al"); assertFalse(name.validate()); assertEquals("min", name.errorMessage)
        name.change(" Alice "); assertTrue(name.validate()); assertEquals("Alice", name.getData())
    }
}
```
`BaseFieldState` uses `mutableStateOf`, which works on the JVM without a Compose runtime host.

### Template: Compose component test (Robolectric, after adding ui-test deps)
```kotlin
@RunWith(RobolectricTestRunner::class)
class BlockButtonComponentTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun disabledButtonIgnoresClicksAndExposesReason() {
        var clicks = 0
        compose.setContent {
            Theme {
                BlockButtonComponent(
                    modifier = Modifier, blockButtonStyleType = BlockButtonStyleType.Primary,
                    blockButtonSizeType = BlockButtonSizeType.Medium, text = "Pay", leadingIcon = null, trailingIcon = null,
                    enabled = false, accessibilityState = AccessibilityState.Click("Fill the form"), onClick = { clicks++ },
                )
            }
        }
        compose.onNodeWithText("Pay").assertIsNotEnabled().performClick()
        assertEquals(0, clicks)
        compose.onNodeWithText("Pay").assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Fill the form"))
    }
}
```
Always wrap content in `Theme { }`, because components read `LocalPalette`/`LocalDimensions`/`LocalTypographies` and crash without them. Components that read
`LocalBottomNavBarScrollConnection` (for example `Modifier.bottomNavBarPadding()`) also need
`CompositionLocalProvider(LocalBottomNavBarScrollConnection provides rememberBottomNavBarScrollConnection())`.

### Good first regression tests (they cover known bugs)
- `CheckboxComponent`/`ToggleComponent` toggle with `onValueChange = null` (currently fails).
- `AmountFieldComponent` with `CurrencyType.Symbol` (currently throws).
- `TextFieldState` `MinSize` on a `null` value (currently NPE).
- `NavigationGraphFactoryImpl` launcher validation and `getNavigationGraphFromDestination` (they're internal, so test from inside the module).

## Related pages

- [core-data testing](../core-data/testing.md)
- [Base architecture](base-architecture.md)
- [Forms](forms.md)
- [Overview (build setup)](overview.md#build-setup)
