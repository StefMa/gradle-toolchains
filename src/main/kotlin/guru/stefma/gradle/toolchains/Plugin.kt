package guru.stefma.gradle.toolchains

import guru.stefma.gradle.toolchains.distributions.JavaDistribution
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.internal.SettingsInternal
import org.gradle.jvm.toolchain.JavaToolchainDownload
import org.gradle.jvm.toolchain.JavaToolchainRequest
import org.gradle.jvm.toolchain.JavaToolchainResolver
import org.gradle.jvm.toolchain.JavaToolchainResolverRegistry
import org.gradle.kotlin.dsl.jvm
import java.net.URI
import java.util.Optional

public abstract class Plugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        settings.plugins.apply("jvm-toolchain-management")

        val registry = (settings as SettingsInternal).services.get(JavaToolchainResolverRegistry::class.java)
        registry.register(GradleToolchainResolver::class.java)

        settings.toolchainManagement {
            it.jvm {
                javaRepositories {
                    it.repository("guru.stefma.gradle-toolchain") {
                        it.resolverClass.set(GradleToolchainResolver::class.java)
                    }
                }
            }
        }
    }
}

internal abstract class GradleToolchainResolver : JavaToolchainResolver {
    override fun resolve(request: JavaToolchainRequest): Optional<JavaToolchainDownload> {
        val javaDistribution = JavaDistribution(
            request.javaToolchainSpec.vendor.get(),
            request.buildPlatform.architecture,
            request.javaToolchainSpec.languageVersion.get(),
        )
        return Optional.ofNullable(javaDistribution.getInstallationUrl())
            .map { JavaToolchainDownload.fromUri(URI.create(it)) }
    }
}