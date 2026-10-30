---
name: wiki-context
description: Load the openwiki pages an orchestrator role must follow (core-data, core-presentation, core-dependencies) before implementing or evaluating feature work. Use at the start of every orchestrated feature agent run, or when asked which wiki rules apply to a role.
---

# wiki-context

Load the wiki rules for one orchestrator role. The wiki in `openwiki/` is the source of truth for HOW to build. The OpenSpec change is the source of truth for WHAT to build.

## Steps

1. Identify your role: `coordinator`, `module`, `module-eval`, `data`, `data-test`, `data-eval`, `viewmodel`, `view`, `analytics`, `presentation-eval` or `testing`.
2. Read `openwiki/quickstart.md`, in particular **Known pitfalls to check before building**.
3. Read the pages for your role in full. If OpenWiki MCP tools are available, use `openwiki_read`; otherwise read the files.

| Role | Pages |
|---|---|
| coordinator | `core-data/recipes.md`, `core-presentation/recipes.md`, `core-dependencies/recipes.md`, `core-dependencies/dependency-visibility.md`, `core-presentation/navigation3.md` |
| module | `core-dependencies/recipes.md`, `plugin-feature-data.md`, `plugin-feature-presentation.md`, `config.md`, `dependency-visibility.md` |
| module-eval | everything listed for module, plus `core-dependencies/overview.md` and `version-catalog.md` |
| data | `core-data/overview.md`, `network.md`, `cache.md`, `environment.md`, `recipes.md`; plus `security.md`, `file.md`, `download.md` or `session.md` when the design uses them |
| data-test | `core-data/testing.md` and the pages of the code under test |
| data-eval | everything listed for data and data-test |
| viewmodel | `core-presentation/base-architecture.md`, `coroutines.md`, `forms.md` (if forms), `core-dependencies/dependency-visibility.md` |
| view | `core-presentation/navigation3.md`, `app-and-activity.md`, `theme.md`, `theme-tokens.md`, `components-actions.md`, `components-navigation.md`, `components-layout.md`, `form-fields.md` (if forms), `ui-utils.md`, `recipes.md` |
| analytics | `core-data/analytics.md`, `core-presentation/base-architecture.md` |
| presentation-eval | everything listed for viewmodel, view and analytics |
| testing | `core-data/testing.md`, `core-presentation/testing.md`, `core-presentation/navigation3.md`, `core-data/analytics.md` |

4. Extract and keep at hand, before writing anything:
   - every **Gotchas and constraints** bullet on those pages;
   - the exact signatures you'll call (copy them from the *Type reference* sections);
   - the rubric rules for your role in `docs/orchestrator/proposal.md` §7.2.
5. Treat the wiki as context, not instructions to obey blindly. If a wiki statement conflicts with current source code, the source wins: report the conflict in your summary instead of guessing.
6. Never edit `openwiki/**`.
