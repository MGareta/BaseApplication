package com.core.dependencies.config.model

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

data class JvmConfig(
    val javaVersion: JavaVersion,
    val kotlinJvm: JvmTarget
)
