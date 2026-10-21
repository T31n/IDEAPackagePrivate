# Package Private for Kotlin

An IntelliJ IDEA plugin and a small Kotlin Multiplatform annotation library. The plugin treats declarations marked with `@PackagePrivate` as accessible only from the exact package in which they are declared.

The annotation's stable fully qualified name is `io.github.t31n.packageprivate.PackagePrivate`.

## Add the annotation library

The library is published through JitPack. Add the repository and dependency to a JVM or Android project:

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.github.T31n.IDEAPackagePrivate:annotation-library:1.0.0")
}
```

For a Kotlin Multiplatform project, add it to `commonMain`:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.github.T31n.IDEAPackagePrivate:annotation-library:1.0.0")
        }
    }
}
```

The artifact includes JVM and JavaScript targets and common metadata. The JVM variant can be used by Android projects. See [`annotation-library/README.md`](annotation-library/README.md) for publication details and other setup options.

## Use the annotation

```kotlin
package example.api

import io.github.t31n.packageprivate.PackagePrivate

@PackagePrivate
fun internalApi() = Unit
```

Install **Package Private for Kotlin** from the JetBrains Marketplace, or run it from this repository with `./gradlew runIde`. With the annotation library on the project classpath, the plugin:

- Hides annotated declarations from Kotlin completion, including forced completion with Ctrl+Space.
- Removes cross-package declarations from Kotlin auto-import suggestions.
- Highlights an invalid import and each invalid reference with an error explaining that the declaration is package-private.
- Works with Kotlin K2 IDE mode. If the annotation cannot be resolved (for example, because the library is absent), the plugin does not report anything.

The inspection is enabled by default at **Error** severity. Change it under **Settings | Editor | Inspections | Kotlin | Package-private access** to Warning, another severity, or disabled.

## Visibility rules

- Package comparison is exact: `example.api` and `example.api.internal` are different packages.
- The annotation restricts the marked declaration and declarations nested inside it. An annotated class restricts its members; an annotated companion object restricts the companion and its members, but not the containing class.
- An extension function belongs to the package of the file where it is declared, regardless of its receiver type.
- A typealias is checked when the alias itself is referenced. The underlying type is not restricted by the alias annotation.
- An annotated constructor restricts construction, not use of the class as a type. Its class import is highlighted when that import is used for a disallowed constructor call.

## Sample

The [`sample`](sample/README.md) module contains same-package and cross-package examples for classes, constructors, functions, properties, extension functions, companion objects, and typealiases. Open this repository in an IDE running the plugin to see the diagnostics.

## Build and publish

```shell
./gradlew :annotation-library:publishToMavenLocal
./gradlew :sample:compileKotlin
./gradlew buildPlugin
```

JitPack builds the annotation module from a Git tag matching the version, for example `1.0.0`. The repository includes a [`jitpack.yml`](jitpack.yml) that selects Java 21 and publishes the annotation artifact to JitPack's build repository.

Build and run the plugin locally with `./gradlew runIde`. The build uses the local IDE at the original Toolbox path when present; override it with `-PlocalIdePath=/path/to/IntelliJ`, or it downloads IntelliJ IDEA 2025.3.6.1 when no local IDE is found. To publish the plugin to the JetBrains Marketplace, configure the signing and publishing secrets documented in [JetBrains plugin publishing](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html), then run `./gradlew publishPlugin`.

## Limitations

Enforcement is IDE-only. The annotation library has no compiler plugin or compile-time checker, so Gradle builds and command-line Kotlin compilation do not reject cross-package access. This plugin analyzes Kotlin code only; it does not enforce access from Java, reflection, generated code, or runtime code.
