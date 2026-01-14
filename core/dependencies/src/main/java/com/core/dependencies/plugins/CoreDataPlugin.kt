package com.core.dependencies.plugins

import com.android.build.gradle.LibraryExtension
import com.core.dependencies.utils.androidTestApi
import com.core.dependencies.utils.api
import com.core.dependencies.utils.configureBuildTypes
import com.core.dependencies.utils.configureDetekt
import com.core.dependencies.utils.configurePackaging
import com.core.dependencies.utils.coreLibs
import com.core.dependencies.utils.defaultConfiguration
import com.core.dependencies.utils.detektPlugins
import com.core.dependencies.utils.implementation
import com.core.dependencies.utils.ksp
import com.core.dependencies.utils.testApi
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class CoreDataPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val androidLibraryPlugin = coreLibs.findPlugin("android-library").get().get()
            val kotlinAndroidPlugin = coreLibs.findPlugin("kotlin-android").get().get()
            val kotlinSerializablePlugin = coreLibs.findPlugin("kotlin-serializable").get().get()
            val googleKspPlugin = coreLibs.findPlugin("google-ksp").get().get()
            val googleHiltPlugin = coreLibs.findPlugin("google-hilt").get().get()
            val dokkaPlugin = coreLibs.findPlugin("dokka").get().get()
            val detektPlugin = coreLibs.findPlugin("detekt").get().get()

            val kotlinSerializationDependency = coreLibs.findLibrary("kotlin-serialization").get()
            val androidXDatastoreDependency = coreLibs.findLibrary("androidx-datastore").get()
            val hiltAndroidDependency = coreLibs.findLibrary("google-hilt-android").get()
            val hiltCompilerDependency = coreLibs.findLibrary("google-hilt-compiler").get()
            val retrofitDependency = coreLibs.findLibrary("squareup-retrofit").get()
            val retrofitConverterDependency = coreLibs.findLibrary("squareup-retrofit-converter").get()
            val retrofitLoggingInterceptorDependency = coreLibs.findLibrary("squareup-okhttp-logging-interceptor").get()
            val firebaseBomDependency = coreLibs.findLibrary("google-firebase-bom").get()
            val firebaseCrashlyticsDependency = coreLibs.findLibrary("google-firebase-crashlytics").get()
            val firebaseAnalyticsDependency = coreLibs.findLibrary("google-firebase-analytics").get()
            val detektKtlintDependency = coreLibs.findLibrary("detekt-ktlint").get()
            val detektLibraryDependency = coreLibs.findLibrary("detekt-library").get()

            val testUnitBundle = coreLibs.findBundle("test-unit").get()
            val testInstrumentedBundle = coreLibs.findBundle("test-instrumented").get()

            with(pluginManager) {
                apply(androidLibraryPlugin.pluginId)
                apply(kotlinAndroidPlugin.pluginId)
                apply(kotlinSerializablePlugin.pluginId)
                apply(googleKspPlugin.pluginId)
                apply(googleHiltPlugin.pluginId)
                apply(dokkaPlugin.pluginId)
                apply(detektPlugin.pluginId)
            }

            with(extensions.getByType<LibraryExtension>()) {
                defaultConfiguration(extension = this)
                configureBuildTypes()
                configurePackaging()
            }

            configureDetekt()

            dependencies {
                // Kotlin
                api(dependencyNotation = kotlinSerializationDependency)

                // Android
                implementation(dependencyNotation = androidXDatastoreDependency)

                // Hilt
                implementation(dependencyNotation = hiltAndroidDependency)
                ksp(dependencyNotation = hiltCompilerDependency)

                // Squareup
                api(dependencyNotation = retrofitDependency)
                implementation(dependencyNotation = retrofitConverterDependency)
                implementation(dependencyNotation = retrofitLoggingInterceptorDependency)

                // Google
                implementation(dependencyNotation = platform(firebaseBomDependency))
                implementation(dependencyNotation = firebaseCrashlyticsDependency)
                implementation(dependencyNotation = firebaseAnalyticsDependency)

                // Test
                testApi(dependencyNotation = testUnitBundle)
                androidTestApi(dependencyNotation = testInstrumentedBundle)

                // Detekt
                detektPlugins(dependencyNotation = detektKtlintDependency)
                detektPlugins(dependencyNotation = detektLibraryDependency)
            }
        }
    }
}
