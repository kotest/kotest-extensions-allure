import org.gradle.api.tasks.testing.logging.TestExceptionFormat

buildscript {
   repositories {
      jcenter()
      mavenCentral()
      maven {
         url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
      }
      maven {
         url = uri("https://plugins.gradle.org/m2/")
      }
   }
}

plugins {
   java
   `java-library`
   id("java-library")
   id("maven-publish")
   signing
   maven
   `maven-publish`
   kotlin("jvm").version(Libs.kotlinVersion)
   id("io.qameta.allure") version "2.9.6"
}

allprojects {
   apply(plugin = "org.jetbrains.kotlin.jvm")

   group = Libs.org
   version = Ci.version

   dependencies {
      implementation(kotlin("reflect"))
      implementation(Libs.Kotest.common)
      implementation(Libs.Kotest.api)
      implementation("javax.xml.bind:jaxb-api:2.3.1")
      implementation("com.sun.xml.bind:jaxb-core:2.3.0.1")
      implementation("com.sun.xml.bind:jaxb-impl:2.3.6")
      api(Libs.Allure.commons)
      api(Libs.Allure.junit5)
      testImplementation(Libs.Kotest.assertions)
      testImplementation(Libs.Kotest.junit5)
      testImplementation(Libs.Jackson.kotlin)
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
}

allure {
   adapter.autoconfigure.set(false)
   version.set("2.13.2")
}

apply("./publish.gradle.kts")
