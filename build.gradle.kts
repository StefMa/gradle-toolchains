plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    `java-gradle-plugin`
    `maven-publish`
}

kotlin.jvmToolchain {
    vendor.set(JvmVendorSpec.ADOPTIUM)
    languageVersion.set(JavaLanguageVersion.of(11))
}

group = "guru.stefma.gradle.toolchains"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

    compileOnly(gradleApi())

    testImplementation(gradleTestKit())
    testImplementation(platform("org.junit:junit-bom:5.12.2"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("gradle-toolchains") {
            id = "guru.stefma.gradle-toolchains"
            implementationClass = "guru.stefma.gradle.toolchains.Plugin"
        }
    }
}
