@file:Suppress("PropertyName")

import org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask
import java.text.SimpleDateFormat
import java.util.Date

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
val PUBLISH_VERSION: String by extra("1.0.18")

group = PUBLISH_GROUP_ID
version = PUBLISH_VERSION

kotlin {

  ios {
    binaries.framework(PUBLISH_ARTIFACT_ID)
  }

  jvm()

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

    val jvmMain by getting {
      dependencies {
        implementation("io.ktor:ktor-client-cio:$ktorVersion")
        implementation("com.squareup.sqldelight:sqlite-driver:$sqlDelightVersion")
      }
    }
    val jvmTest by getting

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

    fun runGit(vararg options: String) {
      exec {
        workingDir = File(cocoaRepoPath)
        commandLine("git", *options).standardOutput
      }
    }

    register("gitCheckoutCocoaRepoDevelop") {
      group = "git"
      runGit("checkout", "develop")
    }

    register("gitCheckoutCocoaRepoMaster") {
      group = "git"
      runGit("checkout", "master")
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

    fun bumpPodSpecVersion(): Boolean {
      val podspec = File("$cocoaRepoPath/vaccination-tracker.podspec")
      val tempFile = File("$cocoaRepoPath/vaccination-tracker.bak")
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

    register("publishDevFramework") {
      description = "Publish iOs debug framework to the Cocoa Repo"
      group = "iOS publishing"
      dependsOn("gitCheckoutCocoaRepoDevelop", "universalDebugFramework")
      doLast {
        if (bumpPodSpecVersion()) {
          val nowString = SimpleDateFormat("dd/MM/yyyy - HH:mm").format(Date())
          runGit("add", ".")
          runGit("commit", "-m", "New dev release: ${PUBLISH_VERSION}-$nowString")
          runGit("push", "origin", "develop")
        }
      }
    }

    register("publishReleaseFramework") {
      description = "gitCheckoutCocoaRepoDevelop"
      group = "iOS publishing"
      dependsOn("gitCheckoutCocoaRepoMaster", "universalReleaseFramework")
      doLast {
        if (bumpPodSpecVersion()) {
          runGit("add", ".")
          runGit("commit", "-m", """ "New release: $PUBLISH_VERSION" """)
          runGit("tag", PUBLISH_VERSION)
          runGit("push", "origin", "master", "--tags")
        }
      }
    }

    register("publishAll") {
      description =
        "Publish JVM and Android artifacts to MavenCentral and push iOs framework to the Cocoa Repo"
      group = "publish all"
      dependsOn(
        ":vaccination-tracker:publishJvmPublicationToMavenLocal",
        ":vaccination-tracker:publishJvmPublicationToSonatypeRepository",

        ":vaccination-tracker:publishReleasePublicationToMavenLocal",
        ":vaccination-tracker:publishReleasePublicationToSonatypeRepository",

        "publishReleaseFramework"
      )
    }
  }
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
