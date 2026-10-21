# IDE enforcement sample

This module depends on the local `:annotation-library` project so it can be opened and tested before publishing a JitPack release.

- [`demo.api`](src/main/kotlin/demo/api/PackagePrivateApi.kt) contains annotated declarations and [`SamePackageUse.kt`](src/main/kotlin/demo/api/SamePackageUse.kt) shows access allowed from the declaring package.
- [`demo.client`](src/main/kotlin/demo/client/CrossPackageUse.kt) imports and uses those declarations from a different package. The plugin should mark each import and use as an error.

Open the repository in IntelliJ IDEA with the **Package Private for Kotlin** plugin enabled. The Gradle build intentionally still succeeds: this feature is IDE-only and does not add compile-time enforcement.

```shell
./gradlew :sample:compileKotlin
./gradlew runIde
```
