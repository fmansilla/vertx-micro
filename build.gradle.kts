@file:Suppress("PropertyName")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.net.URI

plugins {
    kotlin("jvm") version "1.3.21"
    id("com.github.johnrengelman.shadow") version "2.0.4"
    application
}

object Versions {
    const val KOTLIN = "1.3.21"
    const val VERTX = "3.7.1"
    const val JUNIT = "5.4.2"
}

object App {
    const val launcherClassName = "io.vertx.core.Launcher"
    const  val mainVerticleName = "ar.ferman.vertxmicro.CoroutineHttpVerticle"
//    const  val mainVerticleName = "ar.ferman.vertxmicro.NoCoroutineHttpVerticle"
}

application {
    mainClassName = App.launcherClassName
}

repositories {
    jcenter()
    mavenCentral()
    maven {
        url = URI.create("https://oss.sonatype.org/content/repositories/iovertx-3717/")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8", Versions.KOTLIN))
    implementation(kotlin("reflect", Versions.KOTLIN))

    vertxDependencies()

    loggingDependencies()
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

    //testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.0-M1")
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
