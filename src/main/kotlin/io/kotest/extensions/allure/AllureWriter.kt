package io.kotest.extensions.allure

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.TestPath
import io.kotest.core.descriptors.spec
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.TestResult
import io.qameta.allure.Allure
import io.qameta.allure.AllureLifecycle
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Issue
import io.qameta.allure.Link
import io.qameta.allure.Links
import io.qameta.allure.Owner
import io.qameta.allure.Severity
import io.qameta.allure.SeverityLevel
import io.qameta.allure.Story
import io.qameta.allure.model.Label
import io.qameta.allure.model.Status
import io.qameta.allure.model.StatusDetails
import io.qameta.allure.model.StepResult
import io.qameta.allure.util.ResultsUtils
import java.lang.reflect.InvocationTargetException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

class AllureWriter {

   companion object {
      const val LanguageLabel = "kotlin"
      const val FrameworkLabel = "kotest"
   }

   /**
    * Loads the [AllureLifecycle] object which is used to report test lifecycle events.
    */
   val allure = try {
      Allure.getLifecycle() ?: throw IllegalStateException("Allure lifecycle was null")
   } catch (t: Throwable) {
      t.printStackTrace()
      throw t
   }

   private val uuids = ConcurrentHashMap<TestPath, String>()

   fun id(testCase: TestCase) = uuids[testCase.descriptor.path()]

   fun startTestCase(testCase: TestCase) {
      val labels = listOfNotNull(
         testCase.epic(),
         testCase.feature(),
         ResultsUtils.createFrameworkLabel(FrameworkLabel),
         ResultsUtils.createHostLabel(),
         ResultsUtils.createLanguageLabel(LanguageLabel),
         testCase.owner(),
         ResultsUtils.createPackageLabel(testCase.spec::class.java.`package`.name),
         ResultsUtils.createSuiteLabel(testCase.descriptor.spec().id.value),
         ResultsUtils.createSeverityLabel(testCase.config.severity.convertToSeverity()),
         testCase.story(),
         ResultsUtils.createThreadLabel()
      )

      val links = mutableListOf<io.qameta.allure.model.Link?>()
      testCase.issue()?.let {
         links.add(testCase.issue())
      }
      testCase.link()?.let {
         links.add(testCase.link())
      }
      testCase.links()?.forEach {
         links.add(ResultsUtils.createLink(it))
      }
      val uuid = UUID.randomUUID().toString()
      uuids[testCase.descriptor.path()] = uuid

      val testName = testCase.constructName()
      val result = io.qameta.allure.model.TestResult()
         .setFullName(testName)
         .setName(testName)
         .setUuid(uuid)
         .setTestCaseId(safeId(testCase.descriptor))
         .setHistoryId(safeId(testCase.descriptor))
         .setLabels(labels)
         .setLinks(links)
         .setDescription(testCase.description())

      allure.scheduleTestCase(result)
      allure.startTestCase(uuid)
   }

   fun finishTestCase(testCase: TestCase, result: TestResult) {
      val status = when (result) {
         // what we call an error, allure calls broken
         is TestResult.Error -> Status.BROKEN
         is TestResult.Failure -> Status.FAILED
         is TestResult.Ignored -> Status.SKIPPED
         is TestResult.Success -> Status.PASSED
      }

      val uuid = uuids[testCase.descriptor.path()]
      val details = ResultsUtils.getStatusDetails(result.errorOrNull)

      allure.updateTestCase(uuid) {
         it.status = status
         it.statusDetails = details.orElseGet { null }
         testCase.descriptor.parents().forEach { d ->
            it.steps.add(
               StepResult()
                  .setName(d.id.value)
                  .setStatus(Status.PASSED)
                  .setStart(0L)
                  .setStop(0L)
            )
         }
      }
      allure.stopTestCase(uuid)
      allure.writeTestCase(uuid)
   }

   private fun links(kclass: KClass<out Spec>): List<io.qameta.allure.model.Link?> {
      val links = mutableListOf<io.qameta.allure.model.Link?>()
      kclass.issue()?.let {
         links.add(kclass.issue())
      }
      kclass.link()?.let {
         links.add(kclass.link())
      }
      kclass.links()?.forEach {
         links.add(ResultsUtils.createLink(it))
      }
      return links.toList()
   }

   fun allureResultSpecInitFailure(kclass: KClass<out Spec>, t: Throwable) {
      val uuid = UUID.randomUUID()
      val labels = listOfNotNull(
         ResultsUtils.createSuiteLabel(kclass.qualifiedName),
         ResultsUtils.createThreadLabel(),
         ResultsUtils.createHostLabel(),
         ResultsUtils.createLanguageLabel("kotlin"),
         ResultsUtils.createFrameworkLabel("kotest"),
         ResultsUtils.createPackageLabel(kclass.java.`package`.name),
         kclass.severity(),
         kclass.owner(),
         kclass.epic(),
         kclass.feature(),
         kclass.story()
      )

      val links = links(kclass)

      val result = io.qameta.allure.model.TestResult()
         .setFullName(kclass.qualifiedName)
         .setName(kclass.simpleName)
         .setUuid(uuid.toString())
         .setLabels(labels)
         .setLinks(links)

      allure.scheduleTestCase(result)
      allure.startTestCase(uuid.toString())

      val instanceError = (t.cause as InvocationTargetException).targetException

      val details = StatusDetails()
      details.message = instanceError?.message ?: "Unknown error"
      var trace = ""
      instanceError.stackTrace?.forEach {
         trace += "$it\n"
      }
      details.trace = trace

      allure.updateTestCase(uuid.toString()) {
         it.status = Status.FAILED
         it.statusDetails = details
      }
      allure.stopTestCase(uuid.toString())
      allure.writeTestCase(uuid.toString())
   }

   // returns an id that's acceptable in format for allure
   private fun safeId(descriptor: Descriptor.TestDescriptor): String = descriptor.path(true).value
}

fun TestCase.epic(): Label? = this.spec::class.findAnnotation<Epic>()?.let { ResultsUtils.createEpicLabel(it.value) }
fun TestCase.feature(): Label? =
   this.spec::class.findAnnotation<Feature>()?.let { ResultsUtils.createFeatureLabel(it.value) }

fun TestCase.severity(): Label? =
   this.spec::class.findAnnotation<Severity>()?.let { ResultsUtils.createSeverityLabel(it.value) }

fun TestCase.story(): Label? = this.spec::class.findAnnotation<Story>()?.let { ResultsUtils.createStoryLabel(it.value) }
fun TestCase.owner(): Label? = this.spec::class.findAnnotation<Owner>()?.let { ResultsUtils.createOwnerLabel(it.value) }
fun TestCase.issue() = spec::class.findAnnotation<Issue>()?.let { ResultsUtils.createIssueLink(it.value) }
fun TestCase.description() = spec::class.findAnnotation<io.qameta.allure.Description>()?.value
fun TestCase.link() = spec::class.findAnnotation<Link>()?.let { ResultsUtils.createLink(it) }
fun TestCase.links() = spec::class.findAnnotation<Links>()?.value

fun TestCase.constructName(): String {
   return listOfNotNull(
      parent?.constructName(),
      name.prefix,
      name.testName,
      name.suffix
   ).joinToString(separator = " ") { it.trim() }
}

fun TestCaseSeverityLevel.convertToSeverity(): SeverityLevel? = when (this) {
   TestCaseSeverityLevel.BLOCKER -> SeverityLevel.BLOCKER
   TestCaseSeverityLevel.CRITICAL -> SeverityLevel.CRITICAL
   TestCaseSeverityLevel.NORMAL -> SeverityLevel.NORMAL
   TestCaseSeverityLevel.MINOR -> SeverityLevel.MINOR
   TestCaseSeverityLevel.TRIVIAL -> SeverityLevel.TRIVIAL
   else -> null
}

fun KClass<out Spec>.epic(): Label? = this.findAnnotation<Epic>()?.let { ResultsUtils.createEpicLabel(it.value) }
fun KClass<out Spec>.feature(): Label? =
   this.findAnnotation<Feature>()?.let { ResultsUtils.createFeatureLabel(it.value) }

fun KClass<out Spec>.severity(): Label? =
   this.findAnnotation<Severity>()?.let { ResultsUtils.createSeverityLabel(it.value) }

fun KClass<out Spec>.story(): Label? = this.findAnnotation<Story>()?.let { ResultsUtils.createStoryLabel(it.value) }
fun KClass<out Spec>.owner(): Label? = this.findAnnotation<Owner>()?.let { ResultsUtils.createOwnerLabel(it.value) }
fun KClass<out Spec>.issue() = this.findAnnotation<Issue>()?.let { ResultsUtils.createIssueLink(it.value) }
fun KClass<out Spec>.link() = this.findAnnotation<Link>()?.let { ResultsUtils.createLink(it) }
fun KClass<out Spec>.links() = this.findAnnotation<Links>()?.value
fun KClass<out Spec>.description() = this.findAnnotation<io.qameta.allure.Description>()?.value
