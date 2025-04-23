package guru.stefma.gradle.toolchains.distributions

import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.platform.Architecture

internal sealed interface JavaDistribution {
    val architecture: Architecture
    val version: JavaLanguageVersion

    fun getInstallationUrl(): String?
}

internal fun JavaDistribution(
    vendor: JvmVendorSpec,
    architecture: Architecture,
    version: JavaLanguageVersion
): JavaDistribution = when (vendor) {
    JvmVendorSpec.AZUL -> Zulu(architecture, version)
    JvmVendorSpec.ADOPTIUM -> Adoptium(architecture, version)
    JvmVendorSpec.ADOPTOPENJDK -> Adoptium(architecture, version)
    JvmVendorSpec.AMAZON -> Corretto(architecture, version)
    else -> {
        println("Unknown vendor: $vendor")
        UnknownJavaDistribution()
    }
}

internal val JavaDistribution.logger: Logger
    get() = Logging.getLogger("StefMa Gradle-Toolchain")

internal val JavaDistribution.operatingSystem: String
    get() = System.getProperty("os.name").lowercase()