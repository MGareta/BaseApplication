package com.core.dependencies.plugins

import com.android.build.api.dsl.LibraryExtension
import com.core.dependencies.utils.api
import com.core.dependencies.utils.configureBuildTypes
import com.core.dependencies.utils.configureDetekt
import com.core.dependencies.utils.configurePackaging
import com.core.dependencies.utils.coreLibs
import com.core.dependencies.utils.defaultConfiguration
import com.core.dependencies.utils.detektPlugins
import com.core.dependencies.utils.implementation
import com.core.dependencies.utils.ksp
import com.core.dependencies.utils.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class FeatureDataPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val androidLibraryPlugin = coreLibs.findPlugin("android-library").get().get()
            val kotlinSerializablePlugin = coreLibs.findPlugin("kotlin-serializable").get().get()
            val googleKspPlugin = coreLibs.findPlugin("google-ksp").get().get()
            val googleHiltPlugin = coreLibs.findPlugin("google-hilt").get().get()
            val detektPlugin = coreLibs.findPlugin("detekt").get().get()

            val kotlinMetadataDependency = coreLibs.findLibrary("kotlin-metadata").get()
            val hiltAndroidDependency = coreLibs.findLibrary("google-hilt-android").get()
            val hiltCompilerDependency = coreLibs.findLibrary("google-hilt-compiler").get()
            val detektKtlintDependency = coreLibs.findLibrary("detekt-ktlint").get()
            val detektLibraryDependency = coreLibs.findLibrary("detekt-library").get()

            with(pluginManager) {
                apply(androidLibraryPlugin.pluginId)
                apply(kotlinSerializablePlugin.pluginId)
                apply(googleKspPlugin.pluginId)
                apply(googleHiltPlugin.pluginId)
                apply(detektPlugin.pluginId)
            }

            with(extensions.getByType<LibraryExtension>()) {
                defaultConfiguration(extension = this)
                configureBuildTypes()
                configurePackaging()
            }

            configureDetekt()

            dependencies {
                // Core
                api(dependencyNotation = project(projectPath = ":core:data"))

                // Kotlin
                ksp(dependencyNotation = kotlinMetadataDependency)

                // Hilt
                implementation(dependencyNotation = hiltAndroidDependency)
                ksp(dependencyNotation = hiltCompilerDependency)

                // Test
                testImplementation(dependencyNotation = testFixtures(notation = project(projectPath = ":core:data")))

                // Detekt
                detektPlugins(dependencyNotation = detektKtlintDependency)
                detektPlugins(dependencyNotation = detektLibraryDependency)
            }
        }
    }
}
