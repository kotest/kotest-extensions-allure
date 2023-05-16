import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
   id("kotest-publishing-conventions")
   alias(libs.plugins.kotlinJvm).apply(true)
   alias(libs.plugins.allure).apply(true)
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



java {
   toolchain {
      languageVersion.set(JavaLanguageVersion.of(11))
   }
}



tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
   kotlinOptions.jvmTarget = "11"
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
   adapter.autoconfigureListeners.set(false)
   version.set(libs.versions.allure)
}
