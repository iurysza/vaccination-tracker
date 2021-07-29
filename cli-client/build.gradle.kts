@file:Suppress("PropertyName", "SpellCheckingInspection")

plugins {
  application
  kotlin("jvm")
}

val PUBLISH_GROUP_ID: String by extra("com.github.iurysza")
val PUBLISH_ARTIFACT_ID: String by extra("vaccination-tracker-cli")
val PUBLISH_VERSION: String by extra("1.0.17")

group = PUBLISH_GROUP_ID
version = PUBLISH_VERSION

repositories {
  mavenCentral()
  mavenLocal()
}

val coroutinesVersion: String by project
val cliktVersion = "3.2.0"

dependencies {
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
  implementation("com.github.ajalt.clikt:clikt:$cliktVersion")
  implementation("com.github.iurysza:vaccination-tracker-jvm:$PUBLISH_VERSION")
}

application {
  mainClass.set("com.github.iurysza.vaccinationtracker.cli.MainKt")
}

val fatJar = task("fatJar", type = Jar::class) {
  baseName = "${PUBLISH_ARTIFACT_ID}-fat"
  manifest {
    attributes["Implementation-Title"] = PUBLISH_ARTIFACT_ID
    attributes["Implementation-Version"] = version
    attributes["Main-Class"] = "com.github.iurysza.vaccinationtracker.cli.MainKt"
  }
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
  from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
  with(tasks.jar.get() as CopySpec)
}

tasks {
  "build" {
    dependsOn(fatJar)
  }
}
