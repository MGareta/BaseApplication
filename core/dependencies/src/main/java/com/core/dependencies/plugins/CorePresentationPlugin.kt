package com.core.dependencies.plugins

import com.android.build.gradle.LibraryExtension
import com.core.dependencies.utils.androidTestApi
import com.core.dependencies.utils.api
import com.core.dependencies.utils.configureBuildTypes
import com.core.dependencies.utils.configureDetekt
import com.core.dependencies.utils.configurePackaging
import com.core.dependencies.utils.coreLibs
import com.core.dependencies.utils.debugApi
import com.core.dependencies.utils.defaultConfiguration
import com.core.dependencies.utils.detektPlugins
import com.core.dependencies.utils.implementation
import com.core.dependencies.utils.ksp
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.kotlin

class CorePresentationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val androidLibraryPlugin = coreLibs.findPlugin("android-library").get().get()
            val kotlinAndroidPlugin = coreLibs.findPlugin("kotlin-android").get().get()
            val kotlinComposePlugin = coreLibs.findPlugin("kotlin-compose").get().get()
            val kotlinSerializablePlugin = coreLibs.findPlugin("kotlin-serializable").get().get()
            val googleKspPlugin = coreLibs.findPlugin("google-ksp").get().get()
            val googleHiltPlugin = coreLibs.findPlugin("google-hilt").get().get()
            val detektPlugin = coreLibs.findPlugin("detekt").get().get()

            val kotlinSerializationDependency = coreLibs.findLibrary("kotlin-serialization").get()
            val androidXComposeBomDependency = coreLibs.findLibrary("androidx-compose").get()
            val androidXComposeFoundationDependency = coreLibs.findLibrary("androidx-compose-foundation").get()
            val androidXComposePreviewDependency = coreLibs.findLibrary("androidx-compose-preview").get()
            val androidXComposePreviewDebugDependency = coreLibs.findLibrary("androidx-compose-preview-debug").get()
            val androidXMaterial3Dependency = coreLibs.findLibrary("androidx-compose-material3").get()
            val androidXLifecycleViewModelDependency = coreLibs.findLibrary("androidx-lifecycle-viewModel").get()
            val androidXNavigationDependency = coreLibs.findLibrary("androidx-navigation").get()
            val androidXNavigationHiltDependency = coreLibs.findLibrary("androidx-navigation-hilt").get()
            val androidXPagingDependency = coreLibs.findLibrary("androidx-paging").get()
            val hiltAndroidDependency = coreLibs.findLibrary("google-hilt-android").get()
            val hiltCompilerDependency = coreLibs.findLibrary("google-hilt-compiler").get()
            val detektKtlintDependency = coreLibs.findLibrary("detekt-ktlint").get()
            val detektLibraryDependency = coreLibs.findLibrary("detekt-library").get()

            val testInstrumentedBundle = coreLibs.findBundle("test-instrumented").get()

            with(pluginManager) {
                apply(androidLibraryPlugin.pluginId)
                apply(kotlinAndroidPlugin.pluginId)
                apply(kotlinComposePlugin.pluginId)
                apply(kotlinSerializablePlugin.pluginId)
                apply(googleKspPlugin.pluginId)
                apply(googleHiltPlugin.pluginId)
                apply(detektPlugin.pluginId)
            }

            with(extensions.getByType<LibraryExtension>()) {
                defaultConfiguration(extension = this)
                configureBuildTypes()
                configurePackaging()

                buildFeatures {
                    compose = true
                }
            }

            configureDetekt()

            dependencies {
                implementation(dependencyNotation = project(":core:data"))

                // Kotlin
                implementation(dependencyNotation = kotlin("reflect"))
                api(dependencyNotation = kotlinSerializationDependency)

                // AndroidX
                implementation(dependencyNotation = platform(androidXComposeBomDependency))
                api(dependencyNotation = androidXComposeFoundationDependency)
                api(dependencyNotation = androidXComposePreviewDependency)
                debugApi(dependencyNotation = androidXComposePreviewDebugDependency)
                api(dependencyNotation = androidXMaterial3Dependency)
                api(dependencyNotation = androidXLifecycleViewModelDependency)
                api(dependencyNotation = androidXNavigationDependency)
                api(dependencyNotation = androidXNavigationHiltDependency)
                api(dependencyNotation = androidXPagingDependency)

                // Hilt
                implementation(dependencyNotation = hiltAndroidDependency)
                ksp(dependencyNotation = hiltCompilerDependency)

                // Test
                androidTestApi(dependencyNotation = testInstrumentedBundle)

                // Detekt
                detektPlugins(dependencyNotation = detektKtlintDependency)
                detektPlugins(dependencyNotation = detektLibraryDependency)
            }
        }
    }
}
