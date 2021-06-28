import org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")
  id("com.android.library")
  id("kotlin-android-extensions")
  id("com.squareup.sqldelight")
  id("maven-publish")
}

repositories {
  gradlePluginPortal()
  google()
  jcenter()
  mavenCentral()
}

val libName = "kmmpoc"
val libGroup = "com.iurysza.learn"
val libVersionName = "1.0.10"
val libVersionCode = 10000


group = libGroup
version = libVersionName

kotlin {

  ios {
    binaries.framework(libName)
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
    val androidTest by getting {
      dependencies {
        implementation(kotlin("test-junit"))
        implementation("junit:junit:4.12")
      }
    }
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
    register("universalFrameworkDebug", FatFrameworkTask::class) {
      baseName = libName
      from(
        iosArm64().binaries.getFramework(libName, "Debug"),
        iosX64().binaries.getFramework(libName, "Debug")
      )
      destinationDir = buildDir.resolve(cocoaRepoPath)
      group = libName
      description = "Create the debug framework for iOs"
      dependsOn("linkKmmpocDebugFrameworkIosArm64")
      dependsOn("linkKmmpocDebugFrameworkIosX64")
    }

    register("universalFrameworkRelease", FatFrameworkTask::class) {
      baseName = libName
      from(
        iosArm64().binaries.getFramework(libName, "Release"),
        iosX64().binaries.getFramework(libName, "Release")
      )
      destinationDir = buildDir.resolve(cocoaRepoPath)
      group = libName
      description = "Create the release framework for iOs"
      dependsOn("linkKmmpocReleaseFrameworkIosArm64")
      dependsOn("linkKmmpocReleaseFrameworkIosX64")
    }

    register("universalFramework") {
      description = "Create the debug and release framework for iOs"
      dependsOn("universalFrameworkDebug")
      dependsOn("universalFrameworkRelease")
    }

    register("publishDevFramework") {
      description = "Publish iOs framework to the Cocoa Repo"

      project.exec {
        workingDir = File("$cocoaRepoPath")
        commandLine("git", "checkout", "develop").standardOutput
      }

      // Create Release Framework for Xcode
      dependsOn("universalFrameworkDebug")

      // Replace
      doLast {
        val dir = File("$cocoaRepoPath/kmmpoc.podspec")
        val tempFile = File("$cocoaRepoPath/podspec.new")

        val reader = dir.bufferedReader()
        val writer = tempFile.bufferedWriter()
        var currentLine: String?

        while (reader.readLine().also { currLine -> currentLine = currLine } != null) {
          if (currentLine?.startsWith("s.version") == true) {
            writer.write("s.version       = \"${libVersionName}\"" + System.lineSeparator())
          } else {
            writer.write(currentLine + System.lineSeparator())
          }
        }
        writer.close()
        reader.close()
        val successful = tempFile.renameTo(dir)

        if (successful) {

          val dateFormatter = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
          project.exec {
            workingDir = File(cocoaRepoPath)
            commandLine("git", "add", ".").standardOutput
            commandLine(
              "git",
              "commit",
              "-m",
              "New dev release: ${libVersionName}-${dateFormatter.format(Date())}"
            ).standardOutput
          }

          project.exec {
            workingDir = File(cocoaRepoPath)
            commandLine("git", "push", "origin", "develop").standardOutput
          }
        }
      }
    }

    register("publishFramework") {
      description = "Publish iOs framework to the Cocoa Repo"

      project.exec {
        workingDir = File(cocoaRepoPath)
        commandLine("git", "checkout", "master").standardOutput
      }

      // Create Release Framework for Xcode
      dependsOn("universalFrameworkRelease")

      // Replace
      doLast {
        val dir = File("$cocoaRepoPath/kmmpoc.podspec")
        val tempFile = File("$cocoaRepoPath/podspec.new")

        val reader = dir.bufferedReader()
        val writer = tempFile.bufferedWriter()
        var currentLine: String?

        while (reader.readLine().also { currLine -> currentLine = currLine } != null) {
          if (currentLine?.startsWith("s.version") == true) {
            writer.write("s.version       = \"${libVersionName}\"" + System.lineSeparator())
          } else {
            writer.write(currentLine + System.lineSeparator())
          }
        }
        writer.close()
        reader.close()
        val successful = tempFile.renameTo(dir)

        if (successful) {

          project.exec {
            workingDir = File(cocoaRepoPath)
            commandLine("git", "commit", "-a", "-m", "\"New release: ${libVersionName}\"").standardOutput
          }

          project.exec {
            workingDir = File(cocoaRepoPath)
            commandLine("git", "tag", libVersionName).standardOutput
          }

          project.exec {
            workingDir = File(cocoaRepoPath)
            commandLine("git", "push", "origin", "master", "--tags").standardOutput
          }
        }
      }
    }

    register("publishAll") {
      description = "Publish JVM and Android artifacts to Nexus and push iOs framweork to the Cocoa Repo"
      // Publish JVM and Android artifacts to Nexus
      dependsOn("publish")
      // Create Release Framework for Xcode
      dependsOn("universalFrameworkRelease")

      // Replace
      doLast {
        val dir = file("$cocoaRepoPath/kmmpoc.podspec")
        val tempFile = file("$cocoaRepoPath/podspec.new")

        val reader = dir.bufferedReader()
        val writer = tempFile.bufferedWriter()
        var currentLine: String?

        while (reader.readLine().also { currLine -> currentLine = currLine } != null) {
          if (currentLine?.startsWith("s.version") == true) {
            writer.write("s.version       = \"${libVersionName}\"" + System.lineSeparator())
          } else {
            writer.write(currentLine + System.lineSeparator())
          }
        }
        writer.close()
        reader.close()
        val successful = tempFile.renameTo(dir)

        if (successful) {
          project.exec {
            workingDir = File(cocoaRepoPath)
            commandLine("git", "commit", "-a", "-m", "\"New release: ${libVersionName}\"").standardOutput
          }

          project.exec {
            workingDir = File(cocoaRepoPath)
            commandLine("git", "tag", libVersionName).standardOutput
          }

          project.exec {
            workingDir = File(cocoaRepoPath)
            commandLine("git", "push", "origin", "master", "--tags").standardOutput
          }
          println("""""git", "commit", "-a", "-m", "\"New release: ${libVersionName}\"""")
        }
      }
    }
  }
}

android {
  compileSdkVersion(29)
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  defaultConfig {
    minSdkVersion(24)
    targetSdkVersion(29)
    versionCode = 1
    versionName = "1.0"
  }
  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
    }
  }
}

sqldelight {
  database("HackernewsDatabase") {
    packageName = "com.iurysza.learn.kmmpoc.shared.cache"
  }
}
