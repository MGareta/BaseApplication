---
name: allowlist-derive
description: Regenerate docs/orchestrator/allowlist.yaml from the current openwiki pages after the wiki changes. Use when eval-mechanical preflight reports PRE-AL (stale allowlist) or after core modules or the version catalog change.
---

# allowlist-derive

The allowlist encodes "only technology documented in the wiki" as checkable import prefixes, base types, annotations and forbidden symbols
(`docs/orchestrator/proposal.md` §7.1). Regenerate it whenever `openwiki/.last-update.json` is newer than its `source_wiki_update` stamp.

## Method

1. Read `openwiki/core-dependencies/dependency-visibility.md` → *Compile-time visibility (main source set)*. For the **feature data** and **feature presentation** columns, collect
   every visible library. Map each library to its Kotlin package roots (for example Retrofit → `retrofit2.`, OkHttp → `okhttp3.`, Navigation3 → `androidx.navigation3.`).
2. Read `openwiki/core-dependencies/version-catalog.md` to confirm coordinates and catch new or removed libraries.
3. Read the *Package map* of `openwiki/core-data/overview.md` and `openwiki/core-presentation/overview.md` and the *Type reference* sections of each page. Collect public base
   types and annotations features are expected to use into `base_types` and `annotations`.
4. Collect forbidden items from every *Gotchas* section: legacy navigation, commented-out components, known-bug APIs. Add them to `forbidden_imports` or `forbidden_symbols` with the rubric rule ID.
5. Keep the file's existing structure and key names; `scripts/orchestrator/eval-mechanical.py` parses this simple YAML subset (top-level section → key → list of strings, or `- pattern:` / `rule:` pairs).
6. Set `source_wiki_update` to the date in `openwiki/.last-update.json` (`updatedAt`, first 10 characters) and list the pages used under `sources`.
7. Show the human a diff of `docs/orchestrator/allowlist.yaml` and get approval before committing. Never widen the allowlist to make a failing feature pass: that's a scope change (proposal §3.6).
8. Sanity-check it: `python3 -I scripts/orchestrator/eval-mechanical.py --change x --feature demo --gate G2` must parse without errors. Findings about the demo module are expected.
