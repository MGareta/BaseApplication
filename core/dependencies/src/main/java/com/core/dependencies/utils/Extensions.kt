package com.core.dependencies.utils

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.core.dependencies.config.Config
import dev.detekt.gradle.Detekt
import dev.detekt.gradle.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

val Project.coreLibs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("coreLibs")

@Suppress("UnstableApiUsage")
fun Project.defaultConfiguration(extension: ApplicationExtension) {
    extension.apply {
        compileSdk {
            version = release(Config.android.compileSdkVersion)
        }

        defaultConfig {
            minSdk = Config.android.minSdkVersion
            targetSdk = Config.android.targetSdkVersion

            namespace = Config.android.nameSpace
        }

        compileOptions {
            sourceCompatibility = Config.jvm.javaVersion
            targetCompatibility = Config.jvm.javaVersion
        }

        testFixtures {
            enable = true
        }

        kotlinExtension.apply {
            jvmToolchain(Config.jvm.kotlinJvm.target.toInt())
        }
    }
}

@Suppress("UnstableApiUsage")
fun Project.defaultConfiguration(extension: LibraryExtension) {
    extension.apply {
        compileSdk {
            version = release(Config.android.compileSdkVersion)
        }

        defaultConfig {
            minSdk = Config.android.minSdkVersion

            namespace = Config.android.nameSpace
        }

        compileOptions {
            sourceCompatibility = Config.jvm.javaVersion
            targetCompatibility = Config.jvm.javaVersion
        }

        testFixtures {
            enable = true
        }

        kotlinExtension.apply {
            jvmToolchain(Config.jvm.kotlinJvm.target.toInt())
        }
    }
}

fun Project.configureDetekt() {
    val detektDirectory = rootProject.layout.buildDirectory
        .dir("reports")
        .get()
        .dir("detekt")
        .asFile

    if (!detektDirectory.exists()) {
        detektDirectory.mkdir()
    }

    val reportFileName = path
        .removePrefix(
            prefix = ":"
        ).replace(
            oldChar = ':',
            newChar = '_'
        )

    extensions.getByType<DetektExtension>().apply {
        autoCorrect.set(true)
        parallel.set(true)
        source.setFrom(projectDir)

        tasks.withType<Detekt>().configureEach {
            reports {
                html.required.set(true)
                checkstyle.required.set(false)
                sarif.required.set(false)
                markdown.required.set(false)

                html.outputLocation.set(file("$detektDirectory/$reportFileName.html"))
            }
        }
    }
}

fun CommonExtension.configureBuildTypes() {
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

fun CommonExtension.configurePackaging() {
    packaging.resources {
        excludes += "/META-INF/{AL2.0,LGPL2.1}"
        excludes += "META-INF/LICENSE.md"
        excludes += "META-INF/LICENSE-notice.md"
    }
}

fun DependencyHandler.ksp(dependencyNotation: Any): Dependency? = add("ksp", dependencyNotation)

fun DependencyHandler.implementation(dependencyNotation: Any): Dependency? = add("implementation", dependencyNotation)

fun DependencyHandler.api(dependencyNotation: Any): Dependency? = add("api", dependencyNotation)

fun DependencyHandler.testImplementation(dependencyNotation: Any): Dependency? = add("testImplementation", dependencyNotation)

fun DependencyHandler.testFixturesApi(dependencyNotation: Any): Dependency? = add("testFixturesApi", dependencyNotation)

fun DependencyHandler.debugApi(dependencyNotation: Any): Dependency? = add("debugImplementation", dependencyNotation)

fun DependencyHandler.detektPlugins(dependencyNotation: Any): Dependency? = add("detektPlugins", dependencyNotation)
