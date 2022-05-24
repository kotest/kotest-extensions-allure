import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
   `java-library`
   `maven-publish`
   signing
   kotlin("jvm") version "1.6.10"
   id("io.qameta.allure") version "2.9.6"
}

group = "io.kotest.extensions"
version = Ci.version

dependencies {
   implementation(kotlin("reflect"))
   implementation(libs.kotest.framework.api)
   implementation(libs.bundles.jaxb)
   api(libs.allure.commons)
   api(libs.allure.junit5)
   testImplementation(libs.kotest.assertions.core)
   testImplementation(libs.kotest.runner.junit5)
   testImplementation(libs.jackson.module.kotlin)
}

tasks.named<Test>("test") {
   useJUnitPlatform()
   testLogging {
      showExceptions = true
      showStandardStreams = true
      exceptionFormat = TestExceptionFormat.FULL
   }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
   kotlinOptions.jvmTarget = "1.8"
}

repositories {
   mavenLocal()
   mavenCentral()
   maven {
      url = uri("https://oss.sonatype.org/content/repositories/snapshots")
   }
}

allure {
   adapter.autoconfigure.set(false)
   version.set("2.13.2")
}

apply("./publish.gradle.kts")
