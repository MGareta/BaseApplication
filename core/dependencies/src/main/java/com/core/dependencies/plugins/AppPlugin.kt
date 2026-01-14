package com.core.dependencies.plugins

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.TestedExtension
import com.core.dependencies.config.Config
import com.core.dependencies.utils.configureBuildTypes
import com.core.dependencies.utils.configureDetekt
import com.core.dependencies.utils.configurePackaging
import com.core.dependencies.utils.coreLibs
import com.core.dependencies.utils.defaultConfiguration
import com.core.dependencies.utils.detektPlugins
import com.core.dependencies.utils.implementation
import com.core.dependencies.utils.ksp
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AppPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val androidApplicationPlugin = coreLibs.findPlugin("android-application").get().get()
            val kotlinAndroidPlugin = coreLibs.findPlugin("kotlin-android").get().get()
            val kotlinComposePlugin = coreLibs.findPlugin("kotlin-compose").get().get()
            val googleKspPlugin = coreLibs.findPlugin("google-ksp").get().get()
            val googleHiltPlugin = coreLibs.findPlugin("google-hilt").get().get()
            val detektPlugin = coreLibs.findPlugin("detekt").get().get()

            val hiltAndroidDependency = coreLibs.findLibrary("google-hilt-android").get()
            val hiltCompilerDependency = coreLibs.findLibrary("google-hilt-compiler").get()
            val detektKtlintDependency = coreLibs.findLibrary("detekt-ktlint").get()

            with(pluginManager) {
                apply(androidApplicationPlugin.pluginId)
                apply(kotlinAndroidPlugin.pluginId)
                apply(kotlinComposePlugin.pluginId)
                apply(kotlinComposePlugin.pluginId)
                apply(googleKspPlugin.pluginId)
                apply(googleHiltPlugin.pluginId)
                apply(detektPlugin.pluginId)
            }

            with(extensions.getByType<ApplicationExtension>()) {
                defaultConfiguration(extension = this as TestedExtension)
                configureBuildTypes()
                configurePackaging()

                defaultConfig {
                    applicationId = Config.android.applicationId
                    versionCode = Config.android.versionCode
                    versionName = Config.android.versionName
                }

                buildTypes {
                    release {
                        isShrinkResources = true

                        signingConfig = signingConfigs.getByName("debug")
                    }

                    debug {
                        applicationIdSuffix = ".debug"
                        versionNameSuffix = "-debug"
                    }
                }

                buildFeatures {
                    compose = true
                }
            }

            configureDetekt()

            dependencies {
                // Core
                implementation(dependencyNotation = project(":core:presentation"))

                // Hilt
                implementation(dependencyNotation = hiltAndroidDependency)
                ksp(dependencyNotation = hiltCompilerDependency)

                // Detekt
                detektPlugins(dependencyNotation = detektKtlintDependency)
            }
        }
    }
}
