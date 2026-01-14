plugins {
    alias(coreLibs.plugins.android.application) apply false
    alias(coreLibs.plugins.android.library) apply false
    alias(coreLibs.plugins.kotlin.android) apply false
    alias(coreLibs.plugins.kotlin.compose) apply false
    alias(coreLibs.plugins.kotlin.serializable) apply false
    alias(coreLibs.plugins.google.ksp) apply false
    alias(coreLibs.plugins.google.hilt) apply false
    alias(coreLibs.plugins.google.crashlytics) apply false
    alias(coreLibs.plugins.dokka) apply false
    alias(coreLibs.plugins.detekt) apply false
}
