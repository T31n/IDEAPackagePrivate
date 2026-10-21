plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation(project(":annotation-library"))
    implementation(kotlin("stdlib"))
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "21"
    targetCompatibility = "21"
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}
