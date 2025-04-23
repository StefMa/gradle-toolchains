package guru.stefma.gradle.toolchains.distributions

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.platform.Architecture
import java.net.URL

/**
 * API definition from https://docs.azul.com/core/install/metadata-api
 */
internal class Zulu(
    override val architecture: Architecture,
    override val version: JavaLanguageVersion
) : JavaDistribution {
    override fun getInstallationUrl(): String? {
        val distributionUrl = "https://api.azul.com/metadata/v1/zulu/packages"

        val os = when {
            operatingSystem == "mac os x" -> "macos"
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
            Architecture.AARCH64 -> "arm"
        }

        val queryParameters = listOf(
            "availability_types=ca",
            "java-version=${version.asInt()}",
            "os=$os",
            "arch=$arch",
            "java-package-type=jdk",
            "javafx-bundled=false",
            "archive-type=zip",
            "latest=true"
        ).joinToString("&")

        val url = buildString {
            append(distributionUrl)
            append("?")
            append(queryParameters)
        }

        logger.info("Zulu distribution URL: $url")

        val rawJson = URL(url).readText()
        val jsonArray = Json.decodeFromString<JsonArray>(rawJson)
        val installationUrl = jsonArray[0]
            .jsonObject["download_url"]
            ?.toString()
            ?.removeSurrounding("\"")

        logger.info("Zulu installation URL: $installationUrl")

        return installationUrl
    }
}
