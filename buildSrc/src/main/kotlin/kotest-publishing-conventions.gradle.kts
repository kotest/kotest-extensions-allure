plugins {
   `java-library`
   `maven-publish`
   signing
}

val signingKey: String? by project
val signingPassword: String? by project


val publications: PublicationContainer = (extensions.getByName("publishing") as PublishingExtension).publications

signing {
   useGpgCmd()
   if (signingKey != null && signingPassword != null) {
      @Suppress("UnstableApiUsage")
      useInMemoryPgpKeys(signingKey, signingPassword)
   }
   if (Ci.isRelease) {
      sign(publications)
   }
}

java {
   withJavadocJar()
   withSourcesJar()
}

publishing {
   repositories {
      maven {
         val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
         val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
         name = "deploy"
         url = if (Ci.isRelease) releasesRepoUrl else snapshotsRepoUrl
         credentials {
            username = System.getenv("OSSRH_USERNAME") ?: ""
            password = System.getenv("OSSRH_PASSWORD") ?: ""
         }
      }
   }

   publications {
      register("mavenJava", MavenPublication::class) {
         from(components["java"])
         pom {
            name.set("kotest-extensions-allure")
            description.set("Kotest Extensions for Allure")
            url.set("http://www.github.com/kotest/kotest-extensions-allure")

            scm {
               connection.set("scm:git:http://www.github.com/kotest/kotest-extensions-allure")
               developerConnection.set("scm:git:http://github.com/sksamuel")
               url.set("http://www.github.com/kotest/kotest-extensions-allure")
            }

            licenses {
               license {
                  name.set("The Apache 2.0 License")
                  url.set("https://opensource.org/licenses/Apache-2.0")
               }
            }

            developers {
               developer {
                  id.set("sksamuel")
                  name.set("Stephen Samuel")
                  email.set("sam@sksamuel.com")
               }
            }
         }

      }
   }
}
