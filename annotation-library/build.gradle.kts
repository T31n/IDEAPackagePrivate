import org.gradle.api.publish.maven.MavenPublication

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("maven-publish")
}

group = "com.github.T31n.IDEAPackagePrivate"
version = rootProject.version

kotlin {
    jvm()
    js {
        nodejs()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(kotlin("stdlib"))
        }
    }
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        pom {
            name.set("Kotlin Package-Private Annotation")
            description.set(
                "A Kotlin Multiplatform annotation for declarations restricted to their declaring package by the IDE plugin."
            )
            url.set("https://github.com/T31n/IDEAPackagePrivate")
            licenses {
                license {
                    name.set("Apache License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    distribution.set("repo")
                }
            }
            developers {
                developer {
                    id.set("t31n")
                }
            }
            scm {
                url.set("https://github.com/T31n/IDEAPackagePrivate")
                connection.set("scm:git:git://github.com/T31n/IDEAPackagePrivate.git")
                developerConnection.set("scm:git:ssh://git@github.com:T31n/IDEAPackagePrivate.git")
            }
        }
    }
}
