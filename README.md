# gradle-toolchains

A *work-in-progress* Gradle plugin to resolve JDKs from various JDK distributions.

## Usage

To make use of the plugin add following to your `settings.gradle[.kts]` file.

```kotlin
// settings.gradle.kts
plugins {
    id("guru.stefma.gradle-toolchains") version "..."
}
```

> **Note:** You can add **multiple** toolchain resolvers!
> You can just apply this plugin *after* the `foojay-resolver` plugin.
> This plugin would only be used/take action in case the `foojay-resolver` can't locate the requested JDK.

```kotlin
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "..."
    id("guru.stefma.gradle-toolchains") version "..."
}
```

## Motivation

This is an alternative solution to the [foojay-toolchains](https://github.com/gradle/foojay-toolchains).
It was created in response to reports of occasional outages of the foojay service.

This plugin directly uses the JDK distributions' APIs to resolve the JDKs, 
instead of relying on a service like foojay.

## Supported JDK distributions

The plugin currently supports the following JDK distributions:
- Adoptium
- Amazon Corretto
- Azul Zulu

Contributions to support more distributions are welcome!
