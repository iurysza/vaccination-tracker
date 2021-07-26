@file:Suppress("PropertyName")

import org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")
  id("com.android.library")
  id("com.squareup.sqldelight")
}

repositories {
  gradlePluginPortal()
  google()
  mavenCentral()
}

val PUBLISH_GROUP_ID: String by extra("com.github.iurysza")
val PUBLISH_ARTIFACT_ID: String by extra("vaccination-tracker")
val PUBLISH_VERSION: String by extra("1.0.17")

group = PUBLISH_GROUP_ID
version = PUBLISH_VERSION

kotlin {

  ios {
    binaries.framework(PUBLISH_ARTIFACT_ID)
  }

  android {
    publishAllLibraryVariants()
    publishLibraryVariantsGroupedByFlavor = true
  }

  val ktorVersion: String by project
  val serializationVersion: String by project
  val sqlDelightVersion: String by project
  val coroutinesVersion: String by project

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")
        implementation("io.ktor:ktor-client-serialization:$ktorVersion")
        implementation("io.ktor:ktor-client-core:$ktorVersion")
        implementation("com.squareup.sqldelight:runtime:$sqlDelightVersion")
      }
    }
    val commonTest by getting {
      dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
      }
    }

    val androidMain by getting {
      dependencies {
        implementation("io.ktor:ktor-client-android:$ktorVersion")
        implementation("com.squareup.sqldelight:android-driver:$sqlDelightVersion")
      }
    }
    val androidTest by getting

    val iosMain by getting {
      dependencies {
        implementation("io.ktor:ktor-client-ios:$ktorVersion")
        implementation("com.squareup.sqldelight:native-driver:$sqlDelightVersion")
      }
    }
    val iosTest by getting
  }

  tasks {
    val cocoaRepoPath = "$rootDir/../kmm-poc-cocoa"

    register("gitCheckoutDev") {
      group = "git"
      project.exec {
        workingDir = File(cocoaRepoPath)
        commandLine("git", "checkout", "develop").standardOutput
      }
    }

    register("gitCheckoutMaster") {
      group = "git"
      project.exec {
        workingDir = File(cocoaRepoPath)
        commandLine("git", "checkout", "master").standardOutput
      }
    }

    register("universalDebugFramework", FatFrameworkTask::class) {
      baseName = PUBLISH_ARTIFACT_ID
      group = "iOS universal framework"
      description = "Create the debug framework for iOs"
      from(
        iosArm64().binaries.getFramework(PUBLISH_ARTIFACT_ID, "Debug"),
        iosX64().binaries.getFramework(PUBLISH_ARTIFACT_ID, "Debug")
      )
      destinationDir = buildDir.resolve(cocoaRepoPath)
      dependsOn(
        "linkVaccination-trackerDebugFrameworkIosArm64",
        "linkVaccination-trackerDebugFrameworkIosX64"
      )
    }

    register("universalReleaseFramework", FatFrameworkTask::class) {
      baseName = PUBLISH_ARTIFACT_ID
      group = "iOS universal framework"

      description = "Create the release framework for iOs"
      dependsOn(
        "linkVaccination-trackerReleaseFrameworkIosArm64",
        "linkVaccination-trackerReleaseFrameworkIosX64"
      )
      from(
        iosArm64().binaries.getFramework(PUBLISH_ARTIFACT_ID, "Release"),
        iosX64().binaries.getFramework(PUBLISH_ARTIFACT_ID, "Release")
      )
      destinationDir = buildDir.resolve(cocoaRepoPath)
    }

    register("universalFramework") {
      description = "Create the debug and release framework for iOs"
      group = "iOS universal framework"
      dependsOn(
        "universalDebugFramework",
        "universalReleaseFramework"
      )
    }

    register("publishDevFramework") {
      description = "Publish iOs debug framework to the Cocoa Repo"
      group = "iOS publishing"
      dependsOn("gitCheckoutDev", "universalDebugFramework")
      doLast {
        val isSuccessful = bumpPodSpecVersion(cocoaRepoPath)

        if (isSuccessful) {
          val dateFormatter = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
          project.exec {
            workingDir = File(cocoaRepoPath)
            commandLine("git", "add", ".").standardOutput
          }
          project.exec {
            workingDir = File(cocoaRepoPath)
            commandLine(
              "git",
              "commit",
              "-m",
              "New dev release: ${PUBLISH_VERSION}-${dateFormatter.format(Date())}"
            ).standardOutput
          }
          project.exec {
            workingDir = File(cocoaRepoPath)
            commandLine("git", "push", "origin", "develop").standardOutput
          }
        }
      }
    }

    register("publishReleaseFramework") {
      description = "Publish iOs release framework to the Cocoa Repo"
      group = "iOS publishing"
      dependsOn("gitCheckoutMaster", "universalReleaseFramework")
      doLast {
        val isSuccessful = bumpPodSpecVersion(cocoaRepoPath)

        if (isSuccessful) {
          project.exec {
            workingDir = File(cocoaRepoPath)
            commandLine("git", "add", ".").standardOutput
          }
          project.exec {
            workingDir = File(cocoaRepoPath)
            commandLine("git", "commit", "-m", "\"New release: ${PUBLISH_VERSION}\"").standardOutput
          }
          project.exec {
            workingDir = File(cocoaRepoPath)
            commandLine("git", "tag", PUBLISH_VERSION).standardOutput
          }
          project.exec {
            workingDir = File(cocoaRepoPath)
            commandLine("git", "push", "origin", "master", "--tags").standardOutput
          }
        }
      }
    }

    register("publishAll") {
      description =
        "Publish JVM and Android artifacts to MavenCentral and push iOs framework to the Cocoa Repo"
      group = "publish all"
      dependsOn(
        ":vaccination-tracker:publishReleasePublicationToMavenLocal",
        ":vaccination-tracker:publishReleasePublicationToSonatypeRepository",
        "universalReleaseFramework"
      )
      doLast {
        val isSuccessful = bumpPodSpecVersion(cocoaRepoPath)
        if (isSuccessful) {
          project.exec {
            workingDir = File(cocoaRepoPath)
            commandLine("git", "add", ".").standardOutput
          }
          project.exec {
            workingDir = File(cocoaRepoPath)
            commandLine("git", "commit", "-m", "\"New release: ${PUBLISH_VERSION}\"").standardOutput
          }
          project.exec {
            workingDir = File(cocoaRepoPath)
            commandLine("git", "tag", PUBLISH_VERSION).standardOutput
          }
          project.exec {
            workingDir = File(cocoaRepoPath)
            commandLine("git", "push", "origin", "master", "--tags").standardOutput
          }
          println("""""git", "commit", "-m", "\"New release: ${PUBLISH_VERSION}\"""")
        }
      }
    }
  }
}

fun bumpPodSpecVersion(cocoaRepoPath: String): Boolean {
  val podspec = File("$cocoaRepoPath/kmmpoc.podspec")
  val tempFile = File("$cocoaRepoPath/podspec.bak")
  val reader = podspec.bufferedReader()
  val writer = tempFile.bufferedWriter()
  var currentLine: String?

  while (reader.readLine().also { currLine -> currentLine = currLine } != null) {
    if (currentLine?.startsWith("s.version") == true) {
      writer.write("""s.version       = "$PUBLISH_VERSION"${System.lineSeparator()}""")
    } else {
      writer.write(currentLine + System.lineSeparator())
    }
  }
  writer.close()
  reader.close()
  return tempFile.renameTo(podspec)
}

android {
  compileSdk = 29
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  defaultConfig {
    minSdk = 21
    targetSdk = 29
  }
  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
    }
  }
}

sqldelight {
  database("CovidVaccinationDatabase") {
    packageName = "com.github.iurysza.vaccinationtracker.cache"
  }
}

apply(from = "${rootDir}/gradle/publish-android.gradle")