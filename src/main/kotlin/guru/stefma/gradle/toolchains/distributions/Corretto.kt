package guru.stefma.gradle.toolchains.distributions

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.platform.Architecture
import java.net.URL

/**
 * API definition from https://corretto.github.io/corretto-downloads/latest_links/indexmap_with_checksum.json
 */
internal class Corretto(
    override val architecture: Architecture,
    override val version: JavaLanguageVersion
) : JavaDistribution {
    override fun getInstallationUrl(): String? {
        val distributionUrl = "https://corretto.github.io/corretto-downloads/latest_links/indexmap_with_checksum.json"

        logger.info("Corretto distribution URL: $distributionUrl")

        val json = URL(distributionUrl).readText()

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
            Architecture.AARCH64 -> "aarch64"
        }

        val jsonString = Json.decodeFromString<JsonObject>(json)
        val jdkVersion = jsonString.get(os)
            ?.jsonObject?.get(arch)
            ?.jsonObject?.get("jdk")
            ?.jsonObject?.get(version.asInt().toString())
            ?.jsonObject

        val tarGz = jdkVersion?.get("tar.gz")?.jsonObject?.get("resource")
        val zip = jdkVersion?.get("zip")?.jsonObject?.get("resource")

        val installationUrl = "https://corretto.aws" +
            (
                tarGz?.toString()?.removeSurrounding("\"")
                    ?: zip.toString().removeSurrounding("\"")
                )

        logger.info("Corretto installation URL: $installationUrl")

        return installationUrl
    }
}