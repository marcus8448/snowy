pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net") {
            name = "Fabric"
            content {
                includeGroup("fabric-loom")
                includeGroup("net.fabricmc")
            }
        }
        maven("https://maven.neoforged.net/releases") {
            name = "NeoForge"
        }
    }
}

plugins {
    // required by neoforge
    id("org.gradle.toolchains.foojay-resolver-convention") version("1.0.0")
}

rootProject.name = "Snowy"

include("common")
include("fabric")
include("neoforge")
project(":common").name = "common"
project(":fabric").name = "fabric"
project(":neoforge").name = "neoforge"
