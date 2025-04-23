package guru.stefma.gradle.toolchains.distributions

import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.platform.Architecture

internal class UnknownJavaDistribution(
    override val architecture: Architecture = Architecture.X86,
    override val version: JavaLanguageVersion = JavaLanguageVersion.of(0)
) : JavaDistribution {
    override fun getInstallationUrl(): String? = null
}