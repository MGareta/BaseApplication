package com.core.dependencies.config

import com.core.dependencies.config.model.AndroidConfig
import com.core.dependencies.config.model.JvmConfig
import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

object Config {
    val android = AndroidConfig(
        minSdkVersion = 26,
        targetSdkVersion = 35,
        compileSdkVersion = 35,
        applicationId = "com.base.application",
        versionCode = 1,
        versionName = "1.0",
        nameSpace = "com.base.application",
    )

    val jvm = JvmConfig(
        javaVersion = JavaVersion.VERSION_21,
        kotlinJvm = JvmTarget.JVM_21,
    )
}
