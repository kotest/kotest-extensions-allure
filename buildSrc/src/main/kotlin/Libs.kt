object Libs {

   const val kotlinVersion = "1.6.0"
   const val org = "io.kotest.extensions"

   object Kotest {
      private const val version = "5.0.1"
      const val junit5 = "io.kotest:kotest-runner-junit5-jvm:$version"
      const val common = "io.kotest:kotest-common:$version"
      const val assertions = "io.kotest:kotest-assertions-core:$version"
      const val api = "io.kotest:kotest-framework-api:$version"
   }

   object Allure {
      private const val version = "2.17.0"
      const val commons = "io.qameta.allure:allure-java-commons:$version"
   }

   object Jackson {
      private const val version = "2.12.2"
      const val kotlin = "com.fasterxml.jackson.module:jackson-module-kotlin:$version"
   }

   object Coroutines {
      private const val version = "1.5.2"
      const val coreCommon = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
      const val coreJvm = "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$version"
   }
}
