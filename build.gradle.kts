import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
   id("kotest-publishing-conventions")
   alias(libs.plugins.kotlinJvm)
   alias(libs.plugins.allure)
}

group = "io.kotest.extensions"
version = Ci.version

dependencies {
   implementation(kotlin("reflect"))
   implementation(libs.kotest.framework.api)
   implementation(libs.kotest.framework.engine)
   implementation(libs.bundles.jaxb)
   api(libs.allure.commons)
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
   maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
}

allure {
   version.set(libs.versions.allure)
   adapter.autoconfigure.set(false)
   adapter.autoconfigureListeners.set(false)
   adapter {
      frameworks {
         junit5 {
            enabled.set(false)
         }
      }
   }
}
