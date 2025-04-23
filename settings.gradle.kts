pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
    // Enable this for testing the plugin locally
    // id("guru.stefma.gradle-toolchains") version "VERSION"
}

rootProject.name = "gradle-toolchains"

