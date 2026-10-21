plugins {
    id("java")
    alias(libs.plugins.kotlin.jvm)
    id("org.jetbrains.intellij.platform")
}

group = "com.t31n.ideapp"
version = providers.gradleProperty("projectVersion").get()

val localIdePath = providers.gradleProperty("localIdePath")
    .orElse("${System.getProperty("user.home")}/.local/share/JetBrains/Toolbox/apps/idea-IU-253.31033.145")
    .get()
val localIde = file(localIdePath)

dependencies {
    intellijPlatform {
        if (localIde.isDirectory) {
            local(localIdePath)
        } else {
            intellijIdea("2025.3.6.1")
        }
        bundledPlugin("org.jetbrains.kotlin")
        bundledPlugin("com.intellij.java")
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "253"
        }

        changeNotes = """
            Initial release with Kotlin package-private enforcement.
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