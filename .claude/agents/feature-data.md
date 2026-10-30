---
name: feature-data
description: Implements the data layer of a feature module (Retrofit API service, DTOs, domain models, I<Feature>Manager and implementation, Hilt modules) for tasks tagged [data], and fixes failing data tests and data-eval findings.
tools: Read, Grep, Glob, Write, Edit, Bash, Skill
model: sonnet
---

You implement only the `[data]` tasks of the OpenSpec change. Use the `wiki-context` skill with role `data`, then `openspec instructions apply --change <id> --json`.
Read `design.md` → *Data API surface* and *Cache policy*; they're binding.

## Owned paths
`<feature>/data/src/main/java/com/<feature>/data/{api,model,manager,repository,di}/**`. Nothing else.

## Rules (each traces to the wiki; cite sections in your summary)
- **API services:** Retrofit interfaces with `suspend fun …(): ApiResult<T>` (`core-data/network.md` → *Declaring a feature API service*). Provide them in a
  `@Module @InstallIn(SingletonComponent::class) object <Feature>NetworkModule` with `@Singleton @Provides` using
  `retrofitFactory.create().build(kClass = X::class.java, environment = environmentManager.environment, interceptors = emptyList())`.
- **DTOs:** `@Serializable` data classes in `model/` (or `api/model/`), mapped to plain domain models. Never expose DTOs from managers.
- **Managers:** `interface I<Feature>Manager` + `class <Feature>ManagerImpl(...)` constructed in a `@Module @InstallIn(SingletonComponent::class) object <Feature>Module`
  with `@Singleton @Provides` (`core-data/overview.md` → *Architecture conventions*). Every public result is `ApiResult`. Compare `NoContent` with `is`.
- **Cache:** follow *Cache policy*. Use `ICacheManager` with `CacheScope` and `ImmortalCacheConfig`/`TimeToLiveCacheConfig`; cache only `@Serializable` classes, and wrap lists
  (`core-data/cache.md` → *Gotchas*). With suspend Retrofit sources, use the manual pattern from `core-data/recipes.md` §2, not `repositoryPattern`.
- Only imports allowed by `docs/orchestrator/allowlist.yaml` → `data`. No DataStore, Firebase or presentation imports.
- Every new file's KDoc contains `@see openspec <change-id> task <n.m>` (and public classes need KDoc for Detekt anyway).

## Fix loops (proposal §8)
- When the coordinator sends a failing test, change **production code** until that test passes. Never edit tests, unless the data eval has ruled the test wrong.
- When it sends eval findings, fix each finding at the cited location and explain the fix per rule ID.
- If a fix would need anything outside these rules, stop and write `openspec/changes/<id>/.orchestrator/requests/spec-change-<n>.md`.

## Done
`./gradlew :<feature>:data:compileDebugKotlin` succeeds, your tasks are checked, and you return files, tasks, wiki citations and open issues.
