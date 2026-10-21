# Package Private Annotation

A standalone Kotlin Multiplatform annotation library for marking declarations that should be package-private when used with the **Package Private for Kotlin** IntelliJ plugin.

The annotation's stable fully qualified name is:

```kotlin
io.github.t31n.packageprivate.PackagePrivate
```

It is retained in compiled metadata (`AnnotationRetention.BINARY`) so the IDE plugin can inspect annotated declarations from library dependencies. The artifact has common metadata, JVM, and JavaScript publications; Android projects can use its JVM variant.

## Dependency

The library is published through JitPack. Add JitPack to your repositories:

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

For JVM and Android modules:

```kotlin
dependencies {
    implementation("com.github.T31n.IDEAPackagePrivate:annotation-library:1.0.0")
}
```

For Kotlin Multiplatform shared code:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.github.T31n.IDEAPackagePrivate:annotation-library:1.0.0")
        }
    }
}
```

The coordinate uses the `annotation-library` submodule of the `T31n/IDEAPackagePrivate` GitHub repository. JitPack serves a version after a matching Git tag (such as `1.0.0`) has been pushed and built.

## Usage

```kotlin
package example.api

import io.github.t31n.packageprivate.PackagePrivate

@PackagePrivate
class InternalClient

@PackagePrivate
fun internalFunction() = Unit

@PackagePrivate
val internalProperty = "package-scoped"

class InternalConstructor @PackagePrivate constructor()

@PackagePrivate
typealias InternalName = String
```

The annotation can target classes, functions, properties, constructors, and typealiases. Its visibility rules are enforced by the IntelliJ plugin, not by this library or the Kotlin compiler.

## Local development

This repository includes the annotation module as `:annotation-library`; the sample module depends on it directly:

```shell
./gradlew :annotation-library:publishToMavenLocal
./gradlew :sample:compileKotlin
```

## License

Apache License 2.0. See <https://www.apache.org/licenses/LICENSE-2.0>.
