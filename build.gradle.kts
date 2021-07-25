buildscript {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }

  val kotlinVersion: String by project
  val androidGradleVersion: String by project
  val sqlDelightVersion: String by project

  dependencies {
    classpath("com.android.tools.build:gradle:$androidGradleVersion")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
    classpath("com.squareup.sqldelight:gradle-plugin:$sqlDelightVersion")
    classpath("io.github.gradle-nexus:publish-plugin:1.1.0")
  }
}
apply(plugin = "io.github.gradle-nexus.publish-plugin")
apply(from = "${rootDir}/gradle/publishing.gradle")