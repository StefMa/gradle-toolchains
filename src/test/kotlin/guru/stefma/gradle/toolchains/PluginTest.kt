package guru.stefma.gradle.toolchains

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createFile
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import kotlin.io.path.writeText

class PluginTest {

    @TempDir
    lateinit var testProjectDir: Path

    @OptIn(ExperimentalPathApi::class)
    @ParameterizedTest(name = "Test vendor {0} with java version {1}")
    @MethodSource("provideVendorAndVersion")
    fun `test vendor with version will download correct sdk`(vendor: String, version: Int) {
        testProjectDir.resolve("settings.gradle.kts").apply {
            createFile()
            writeText(
                """
                plugins {
                   id("guru.stefma.gradle-toolchains") version "1.0.0-SNAPSHOT"
                }
                """.trimIndent()
            )
        }

        testProjectDir.resolve("build.gradle.kts").apply {
            createFile()
            writeText(
                """
                    plugins {
                        kotlin("jvm") version "2.1.20"
                    }
                    
                    kotlin {
                        jvmToolchain {
                            languageVersion.set(JavaLanguageVersion.of($version))
                            vendor.set(JvmVendorSpec.$vendor)
                        }
                    }
                """.trimIndent()
            )
        }

        val autoProvisioningPath = Path.of("build/tmp/test/work/.gradle-test-kit/jdks").apply {
            deleteRecursively()
        }

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(testProjectDir.toFile())
            .withArguments("compileKotlin")
            .forwardOutput()
            .build()

        if (autoProvisioningPath.exists()) {
            val cachedJdkDir = autoProvisioningPath.toFile().listFiles()
                .filter { it.isDirectory }
                .firstOrNull { it.name.contains(vendor, ignoreCase = true) && it.name.contains(version.toString()) }

            assert(cachedJdkDir != null) { "Cached JDK dir should not be null for $vendor and version: $version" }
        } else {
            // If not exist, then we should check if its already installed locally
            // via javaToolchains...
            val toolchainBuildOutput = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.toFile())
                .withArguments("-q", "javaToolchains")
                .build()

            println(toolchainBuildOutput.output)
            // TODO: check if the output contains the vendor and version
            assert(vendor == "ADOPTIUM") {
                "Vendor should be ADOPTIUM (because we run on GitHub Action that has Temerun installed by default), but was $vendor"
            }
        }
    }

    companion object {
        @JvmStatic
        fun provideVendorAndVersion(): Stream<Arguments> {
            val vendors = arrayOf("ADOPTIUM", "AZUL", "AMAZON")
            val version = arrayOf(11, 17, 21)
            return Stream.of(*vendors).flatMap { vendor -> Stream.of(*version).map { Arguments.of(vendor, it) } }
        }
    }
}