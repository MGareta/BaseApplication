@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("core/dependencies")

    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("coreLibs") {
            from(files("core/dependencies/gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "BaseApplication"

include(":app")

include(":core:data")
include(":core:presentation")

include(":demo:presentation")
include(":demo:data")
