dependencyResolutionManagement {

    @Suppress("UnstableApiUsage")
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

    versionCatalogs {
        create("coreLibs") {
            from(files("gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "core"
