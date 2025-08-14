import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.1.20"
    application
}

group = "org.wocy"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val javafxVersion = "21.0.2"

dependencies {
    testImplementation(kotlin("test"))

    implementation("org.openjfx:javafx-base:$javafxVersion:win")
    implementation("org.openjfx:javafx-controls:$javafxVersion:win")
    implementation("org.openjfx:javafx-graphics:$javafxVersion:win")

    // https://mvnrepository.com/artifact/io.github.oshai/kotlin-logging-jvm
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.7")
    // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
}

application {
    mainClass.set("org.wocy.MainKt")

    applicationDefaultJvmArgs = listOf(
        "--module-path",
        configurations.runtimeClasspath.get()
            .joinToString(separator = System.getProperty("path.separator")) { it.absolutePath },
        "--add-modules",
        "javafx.controls,javafx.graphics",
    )
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
}
