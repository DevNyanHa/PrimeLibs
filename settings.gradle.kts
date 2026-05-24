pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "PrimeLibs"

include("core")
include("nms:paper-1_21_11")
include("nms:paper-26_1_2")
include("platform:api")
include("platform:bukkit")
include("platform:paper")