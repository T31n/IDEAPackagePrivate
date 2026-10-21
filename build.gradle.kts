plugins {
    id("java")
    alias(libs.plugins.kotlin.jvm)
    id("org.jetbrains.intellij.platform")
}

group = "com.t31n.ideapp"
version = "1.0-SNAPSHOT"

val localIdePath = "${System.getProperty("user.home")}/.local/share/JetBrains/Toolbox/apps/idea-IU-253.31033.145"

// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    intellijPlatform {
        local(localIdePath)
        bundledPlugin("org.jetbrains.kotlin")
        bundledPlugin("com.intellij.java")
    }
}

intellijPlatform {
    pluginVerification {
        ides {
            local(localIdePath)
        }
    }

    pluginConfiguration {
        ideaVersion {
            sinceBuild = "252.25557"
        }

        changeNotes = """
            Initial commit.
        """.trimIndent()
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}