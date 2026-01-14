plugins {
    alias(coreLibs.plugins.app)
}

android {}

dependencies {
    implementation(project(":demo:presentation"))
}
