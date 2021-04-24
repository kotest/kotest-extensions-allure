object Libs {

   const val kotlinVersion = "1.4.32"
   const val org = "io.kotest.extensions"

   object Kotest {
      private const val version = "4.4.3"
      const val Junit5 = "io.kotest:kotest-runner-junit5-jvm:$version"
      const val Assertions = "io.kotest:kotest-assertions-core:$version"
      const val Api = "io.kotest:kotest-framework-api:$version"
   }

   object Allure {
      private const val version = "2.13.9"
      const val commons = "io.qameta.allure:allure-java-commons:$version"
   }

   object Coroutines {
      private const val version = "1.4.3"
      const val coreCommon = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
      const val coreJvm = "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$version"
   }
}
