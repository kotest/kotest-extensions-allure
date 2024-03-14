# kotest-extensions-allure

Kotest extensions for [Allure](http://allure.qatools.ru/).

See [docs](https://kotest.io/docs/extensions/allure.html).

Please create issues on the main kotest [board](https://github.com/kotest/kotest/issues).

[![Build Status](https://github.com/kotest/kotest-extensions-allure/workflows/master/badge.svg)](https://github.com/kotest/kotest-extensions-allure/actions)
[<img src="https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-extensions-allure.svg?label=latest%20release"/>](http://search.maven.org/#search|ga|1|kotest-extensions-allure)
![GitHub](https://img.shields.io/github/license/kotest/kotest-extensions-allure)
[![kotest @ kotlinlang.slack.com](https://img.shields.io/static/v1?label=kotlinlang&message=kotest&color=blue&logo=slack)](https://kotlinlang.slack.com/archives/CT0G9SD7Z)
[<img src="https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/io.kotest.extensions/kotest-extensions-allure.svg?label=latest%20snapshot"/>](https://s01.oss.sonatype.org/content/repositories/snapshots/io/kotest/extensions/kotest-extensions-allure/)

## Changelog

### 1.4.0

* Update to Kotest 5.8.1

### 1.3.0

* Update to Kotest 5.6.0

### 1.2.0

* Update to Kotest 5.3.0

### 1.1.1

* Upgrade kotest to 5.2.2

### 1.1.0

* Upgraded to Allure 2.17, requires Kotlin 1.6 and Kotest 5.x

### 1.0.3

* Fix for NPE when running Kotest in parallel mode #5

### 1.0.2

* Release for Allure 2.14 and JDK8 compatibility

### 1.0.1

* Compatibility with Kotest 4.6.0

### 1.0.0

* Migrated from the main Kotest repo to a standalone repo.
* Removed deprecated typealias 'AllureTestListener' - use 'AllureTestReporter'
