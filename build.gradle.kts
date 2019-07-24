@file:Suppress("PropertyName")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
    kotlin("jvm") version "1.3.21"
    id("com.github.johnrengelman.shadow") version "2.0.4"
    application
}

object Versions {
    const val KOTLIN = "1.3.21"
    const val KOTLIN_COROUTINE = "1.3.0-RC"
    const val VERTX = "3.7.1"
    const val JUNIT = "5.4.2"
}

object App {
    const val launcherClassName = "io.vertx.core.Launcher"
    const val mainVerticleName = "ar.ferman.vertxmicro.CoroutineHttpVerticle"
//    const  val mainVerticleName = "ar.ferman.vertxmicro.NoCoroutineHttpVerticle"
}

application {
    mainClassName = App.launcherClassName
}

allprojects {
    repositories {
        jcenter()
        mavenCentral()
        maven { url = URI.create("https://oss.sonatype.org/content/repositories/iovertx-3717/") }
        maven { url = uri("https://jitpack.io") }
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8", Versions.KOTLIN))
    implementation(kotlin("reflect", Versions.KOTLIN))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.KOTLIN_COROUTINE}")

    vertxDependencies()

    loggingDependencies()

//    implementation("com.amazonaws:aws-java-sdk-dynamodb:1.11.589")
    implementation("software.amazon.awssdk:dynamodb:2.7.2")
    implementation("com.github.fmansilla:dynamodb-kotlin:master-SNAPSHOT")
}

val compileKotlin by tasks.getting(KotlinCompile::class) {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    testImplementation("io.vertx:vertx-web-client:${Versions.VERTX}")
    testImplementation("org.assertj:assertj-core:3.11.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${Versions.JUNIT}")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:${Versions.JUNIT}")

    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.1.0")

    //https://www.testcontainers.org/test_framework_integration/junit_5/
    //https://www.testcontainers.org/test_framework_integration/manual_lifecycle_control/#singleton-containers
    testImplementation("org.testcontainers:testcontainers:1.11.3")
    testImplementation("org.testcontainers:junit-jupiter:1.11.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.KOTLIN_COROUTINE}")
}

val compileTestKotlin by tasks.getting(KotlinCompile::class) {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Run config and redeploy watcher.
tasks.named<JavaExec>("run") {
    doFirst {
        args = listOf(
            "run",
            App.mainVerticleName,
            "--launcher-class=${App.launcherClassName}",
            "--redeploy=src/**/*.*",
            "--on-redeploy=./gradlew classes"
        )
    }
}

// Naming and packaging settings for the "shadow jar".
val shadowJar by tasks.getting(ShadowJar::class) {
    baseName = "vertx-micro"
    version = ""
    classifier = ""

    manifest {
        attributes(
            "Main-Verticle" to App.mainVerticleName
        )
    }
    mergeServiceFiles {
        include("META-INF/services/io.vertx.core.spi.VerticleFactory")
    }
}

fun DependencyHandlerScope.vertxDependencies() {
    implementation("io.vertx:vertx-core:${Versions.VERTX}")
    implementation("io.vertx:vertx-web:${Versions.VERTX}")
    implementation("io.vertx:vertx-lang-kotlin:${Versions.VERTX}")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:${Versions.VERTX}")
}

fun DependencyHandlerScope.loggingDependencies() {
    implementation("org.slf4j:slf4j-api:1.7.26")
//    implementation("ch.qos.logback:logback-classic:1.1.3")

    implementation("org.apache.logging.log4j:log4j-api:2.12.0")
    implementation("org.apache.logging.log4j:log4j-core:2.12.0")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.12.0")
}
