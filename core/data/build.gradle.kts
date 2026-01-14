plugins {
    alias(coreLibs.plugins.core.data)
}

android {
    namespace = "com.core.data"

    defaultConfig {
        testInstrumentationRunner = "com.core.data.DefaultTestRunner"
    }
}

dependencies {}
