pluginManagement {
    val quarkusPlatformVersion: String by settings
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
    plugins {
        id("io.quarkus") version quarkusPlatformVersion
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "quarkus-poc"
