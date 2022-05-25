package io.kotest.extensions.allure

import io.kotest.core.listeners.InstantiationErrorListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import kotlin.reflect.KClass

class AllureTestReporter(private val includeContainers: Boolean = false) : TestListener, InstantiationErrorListener {

   val writer = AllureWriter()

   override suspend fun beforeTest(testCase: TestCase) {
      if (includeContainers || testCase.type == TestType.Test) {
         writer.startTestCase(testCase)
      }
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      if (includeContainers || testCase.type == TestType.Test) {
         writer.finishTestCase(testCase, result)
      }
   }

   override suspend fun instantiationError(kclass: KClass<*>, t: Throwable) {
      writer.allureResultSpecInitFailure(kclass, t)
   }
}
