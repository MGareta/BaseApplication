import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.core.dependencies"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }

    test {
        excludes += "*"
    }
}

dependencies {
    compileOnly(coreLibs.android.gradle)
    compileOnly(coreLibs.kotlin.gradle)
    compileOnly(coreLibs.detekt.gradle)
}

gradlePlugin {
    plugins {
        register("app") {
            id = "app"
            implementationClass = "com.core.dependencies.plugins.AppPlugin"
        }

        register("coreData") {
            id = "core.data"
            implementationClass = "com.core.dependencies.plugins.CoreDataPlugin"
        }

        register("corePresentation") {
            id = "core.presentation"
            implementationClass = "com.core.dependencies.plugins.CorePresentationPlugin"
        }

        register("featureData") {
            id = "feature.data"
            implementationClass = "com.core.dependencies.plugins.FeatureDataPlugin"
        }

        register("featurePresentation") {
            id = "feature.presentation"
            implementationClass = "com.core.dependencies.plugins.FeaturePresentationPlugin"
        }
    }
}
