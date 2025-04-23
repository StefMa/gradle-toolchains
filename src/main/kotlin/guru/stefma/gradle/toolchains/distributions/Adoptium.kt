package guru.stefma.gradle.toolchains.distributions

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.platform.Architecture
import java.net.URL

/**
 * API definition from: https://api.adoptopenjdk.net/q/swagger-ui/
 */
internal class Adoptium(
    override val architecture: Architecture,
    override val version: JavaLanguageVersion
) : JavaDistribution {
    override fun getInstallationUrl(): String? {
        val distributionUrl = "https://api.adoptopenjdk.net/v3/assets/latest/${version.asInt()}/hotspot"

        val os = when {
            operatingSystem == "mac os x" -> "mac"
            operatingSystem.contains("windows") -> "windows"
            operatingSystem == "linux" -> "linux"
            else -> {
                logger.error("Cannot determine OS for requested...")
                return null
            }
        }

        val arch = when (architecture) {
            Architecture.X86 -> "x86"
            Architecture.X86_64 -> "x64"
            Architecture.AARCH64 -> "aarch64"
        }

        val queryParameters = listOf(
            "architecture=$arch",
            "image_type=jdk",
            "jvm_impl=hotspot",
            "os=$os",
            "release_type=ga",
            "vendor=adoptopenjdk",
        ).joinToString("&")

        val url = buildString {
            append(distributionUrl)
            append("?")
            append(queryParameters)
        }

        logger.info("Adoptium distribution URL: $url")

        val rawJson = URL(url).readText()
        val jsonArray = Json.decodeFromString<JsonArray>(rawJson)
        val installationUrl = jsonArray[0]
            .jsonObject["binary"]
            ?.jsonObject
            ?.get("package")
            ?.jsonObject
            ?.get("link")
            ?.toString()
            ?.removeSurrounding("\"")

        logger.info("Adoptium installation URL: $installationUrl")

        return installationUrl
    }
}