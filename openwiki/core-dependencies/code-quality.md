---
type: build reference
title: "Code quality: Detekt and automation"
description: How static analysis and wiki automation are set up. Covers the Detekt plugin applied by every convention plugin, configureDetekt (autoCorrect, parallel, sources, HTML report paths), config/detekt/detekt.yml (rule sets and the strict settings that most often fail new code), the ktlint and libraries rule plugins and which modules get them, how to run Detekt, and the scheduled OpenWiki GitHub workflow.
tags: [core-dependencies, detekt, ktlint, static-analysis, ci, github-actions, openwiki]
verified:
  - by: openwiki/0.7.2
    at: 2026-10-26T16:41:29.588Z
sources:
  - id: openwiki-source-6d4b4e707b8d60b6ccfa3425
    resource: repo://.github/workflows/openwiki-update.yml
  - id: openwiki-source-f8e5a98e0bda8dab4136fe2b
    resource: repo://config/detekt/detekt.yml
  - id: openwiki-source-4a1624a1ef947742c273fc18
    resource: repo://core/dependencies/gradle/libs.versions.toml
  - id: openwiki-source-6e98d638297776b5f81f559d
    resource: repo://core/dependencies/src/main/java/com/core/dependencies/plugins/AppPlugin.kt
  - id: openwiki-source-ebbe359b8fc397d0e2f617b5
    resource: repo://core/dependencies/src/main/java/com/core/dependencies/plugins/FeatureDataPlugin.kt
  - id: openwiki-source-bbb0f73ef5c40bd97bd940fa
    resource: repo://core/dependencies/src/main/java/com/core/dependencies/utils/Extensions.kt
generated: { by: "claude-code", at: "2026-10-26T16:41:29.588Z" }
---

# Code quality: Detekt and automation

## File layout

```
config/detekt/detekt.yml                                  # Detekt configuration (1221 lines, full rule listing)
core/dependencies/src/main/java/com/core/dependencies/
└── utils/Extensions.kt                                   # Project.configureDetekt(), DependencyHandler.detektPlugins()
.github/workflows/openwiki-update.yml                     # scheduled OpenWiki update PR
```

## Detekt wiring

- **Plugin:** `dev.detekt` (catalog alias `detekt`, version `2.0.0-alpha.6`, an **alpha** of Detekt 2). It's declared `apply false` in the root `build.gradle.kts` and applied by
  **all five** convention plugins (`app`, `core.data`, `core.presentation`, `feature.data`, `feature.presentation`). The included build compiles against
  `detekt-gradle` (`compileOnly(coreLibs.detekt.gradle)`) to use `DetektExtension`/`Detekt` types.
- **`Project.configureDetekt()`** (called by every plugin after the Android config):
  1. Computes `rootProject/build/reports/detekt` and calls `mkdir()` on it **at configuration time**. This is non-recursive, so it does nothing if `build/reports` doesn't exist yet.
  2. `reportFileName` = project path without the leading `:` with `:` replaced by `_` (`:core:data` gives `core_data`, `:app` gives `app`, `:demo:presentation` gives `demo_presentation`).
  3. `DetektExtension`: `autoCorrect = true`, `parallel = true`, `source.setFrom(projectDir)` (the **whole module directory**).
  4. For every `Detekt` task: the HTML report goes to `<root>/build/reports/detekt/<reportFileName>.html`. Checkstyle, SARIF and Markdown reports are disabled.
  - It doesn't set `config`, `baseline`, `buildUponDefaultConfig` or `ignoreFailures`. The config file is therefore picked up by the Detekt Gradle plugin's
    default location (`config/detekt/detekt.yml` at the root project), and any finding fails the task. Verify this against the Detekt 2 alpha you're using.
- **Rule plugins** (`detektPlugins` configuration):

  | Convention plugin | `detekt-ktlint` (`dev.detekt:detekt-rules-ktlint-wrapper`) | `detekt-library` (`dev.detekt:detekt-rules-libraries`) |
  |---|---|---|
  | `app` | ✔ | ✘ |
  | `core.data`, `core.presentation`, `feature.data`, `feature.presentation` | ✔ | ✔ |

## `config/detekt/detekt.yml` summary

It's a full generated config listing every rule set. Top-level sections: `config` (`validation: true`, `warningsAsErrors: false`), `processors`, `console-reports`
(excludes `ProjectStatisticsReport`, `ComplexityReport`, `NotificationReport`, `IssuesReport`, `FileBasedIssuesReport`), `comments`, `complexity`, `coroutines`, `empty-blocks`,
`exceptions`, `naming`, `performance`, `potential-bugs`, `style`, `ktlint`, `libraries`. There are about 308 `active: true` and 33 `active: false` entries.

Settings that new code most often hits:

| Rule | Setting |
|---|---|
| `comments.UndocumentedPublicClass` / `UndocumentedPublicFunction` / `UndocumentedPublicProperty` | **active** (tests excluded): public API needs KDoc |
| `comments.CommentOverPrivateFunction` | active |
| `complexity.LongMethod` | 60 lines |
| `complexity.LongParameterList` | 5 function / 6 constructor parameters (composables with many parameters will trip this) |
| `complexity.TooManyFunctions` | 11 per file / per class |
| `complexity.CyclomaticComplexMethod` | 14 |
| `complexity.ComplexCondition` | 3 conditions |
| `style.ReturnCount` | **max 0** (single expression or no explicit returns) |
| `style.ThrowsCount` | **max 0** |
| `style.MagicNumber` | active, ignores `-1, 0, 1, 2`, constants, named arguments, `*.kts` |
| `style.MaxLineLength` | 100 |
| `style.MaxChainedCallsOnSameLine` | 1 |
| `style.WildcardImport` | active (except `java.util.*`) |
| `style.ForbiddenComment` | `FIXME:` and similar markers |
| `naming.FunctionNaming` | `[a-z][a-zA-Z0-9]*`, no `@Composable` exception configured in this file, so PascalCase composables may be reported unless Detekt 2 exempts them by default |
| `naming.TopLevelPropertyNaming` | constants `[A-Z][_A-Z0-9]*` |
| `exceptions.TooGenericExceptionCaught`, `SwallowedException` | active |
| `ktlint` | active, `code_style: intellij_idea`, `autoCorrect: true` on rules |
| `libraries.ForbiddenPublicDataClass`, `LibraryCodeMustSpecifyReturnType`, `LibraryEntitiesShouldNotBePublic` | active (in modules with the libraries plugin) |

## Running

```bash
./gradlew detekt                       # all modules (source-based task)
./gradlew :core:data:detekt            # one module
open build/reports/detekt/core_data.html
```
Because `autoCorrect = true`, running Detekt **rewrites sources** (ktlint formatting). Commit or stash first. The existing code base has many findings (for example the
strict `ReturnCount`/`ThrowsCount` and the libraries rules), so treat Detekt as advisory unless a baseline is added. Detekt isn't wired into `check` by the convention plugins
beyond the Detekt plugin's own defaults.

## OpenWiki workflow (`.github/workflows/openwiki-update.yml`)

| Aspect | Value |
|---|---|
| Triggers | `workflow_dispatch` and daily cron `0 8 * * *` |
| Permissions | `contents: write`, `pull-requests: write` |
| Steps | checkout (pinned SHA, `fetch-depth: 0`, full history needed to diff against the last documented commit), Node 22, `npm install -g openwiki@0.7.2 mermaid@11.16.0 jsdom@29.1.1`, `openwiki code --update --print` (`continue-on-error`), remove `openwiki/.run.json`, then create or update a PR on branch `openwiki/update` titled "docs: update OpenWiki" with paths `openwiki`, `AGENTS.md`, the workflow file and `CLAUDE.md` if present. The job fails at the end if OpenWiki failed. |
| Secrets/env | `OPENAI_API_KEY` (`OPENWIKI_PROVIDER=openai`, `OPENWIKI_MODEL_ID=gpt-5.6-terra`), `OPENWIKI_LANGSMITH_API_KEY`, optional `LANGSMITH_API_KEY` tracing (`LANGCHAIN_PROJECT=openwiki`) |

There's no CI workflow for building, testing or Detekt in the repository. Only this documentation workflow exists.

## Extension guide

- **Point Detekt at the config explicitly / add a baseline:** inside `configureDetekt`, set `config.setFrom(rootProject.file("config/detekt/detekt.yml"))`,
  `buildUponDefaultConfig.set(true)` (then trim `detekt.yml` to overrides) and `baseline.set(file("detekt-baseline.xml"))`, and generate it with `./gradlew detektBaseline`.
- **Exempt composables from naming:** add `ignoreAnnotated: ['Composable']` to `FunctionNaming` (and consider it for `LongParameterList`).
- **New rule plugin:** add a catalog library and `detektPlugins(...)` in the relevant convention plugins.

## Related pages

- [Build helper extensions](extensions.md)
- [Overview](overview.md)
- [Version catalog](version-catalog.md)
