import com.diffplug.gradle.spotless.SpotlessExtension
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val modId = project.property("mod.id").toString()
val modName = project.property("mod.name").toString()
val modVersion = project.property("mod.version").toString()
val modDescription = project.property("mod.description").toString()
val modAuthor = project.property("mod.author").toString()
val modLicense = project.property("mod.license").toString()

plugins {
    id("fabric-loom") version("1.10-SNAPSHOT") apply(false)
    id("net.neoforged.moddev") version("2.0.95") apply(false)
    id("com.diffplug.spotless") version("7.0.4") apply(false)
    id("com.modrinth.minotaur") version("2.8.7") apply(false)
    id("net.darkhax.curseforgegradle") version("1.1.26") apply(false)
}

group = "dev.mlow.mods"
version = modVersion
description = modDescription

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "com.diffplug.spotless")

    group = rootProject.group
    version = rootProject.version
    description = rootProject.description

    extensions.configure<BasePluginExtension> {
        archivesName.set("$modId-${project.name}")
    }

    extensions.configure<JavaPluginExtension> {
        targetCompatibility = JavaVersion.VERSION_21
        sourceCompatibility = JavaVersion.VERSION_21
    }

    tasks.withType<ProcessResources> {
        val properties = mapOf(
            "mod_id" to modId,
            "mod_name" to modName,
            "mod_description" to modDescription,
            "mod_license" to modLicense,
            "mod_author" to modAuthor,
            "mod_version" to project.version,
            "min_minecraft" to project.property("minecraft.version.min"),
            "min_fabric_loader" to project.property("fabric.loader.version.min"),
            "min_neoforge" to project.property("neoforge.version.min"),
            "min_fml" to project.property("fml.version.min"),
        )

        inputs.properties(properties)
        filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "META-INF/neoforge.mods.toml", "*.mixins.json")) {
            expand(properties)
        }

        // Minify json resources
        // https://stackoverflow.com/questions/41028030/gradle-minimize-json-resources-in-processresources#41029113
        doLast {
            fileTree(
                mapOf(
                    "dir" to outputs.files.asPath,
                    "includes" to listOf("**/*.json", "**/*.mcmeta")
                )
            ).forEach { file: File ->
                file.writeText(groovy.json.JsonOutput.toJson(groovy.json.JsonSlurper().parse(file)))
            }
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

    tasks.withType<Jar> {
        from(rootProject.file("LICENSE")) {
            rename { "${it}_${modId}"}
        }

        manifest {
            attributes(
                "Specification-Title" to modId,
                "Specification-Vendor" to modAuthor,
                "Specification-Version" to modVersion,
                "Implementation-Title" to project.name,
                "Implementation-Version" to "${project.version}",
                "Implementation-Vendor" to modAuthor,
                "Implementation-Timestamp" to LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                "Maven-Artifact" to "${project.group}:${modName}:${project.version}",
                "Built-On-Java" to "${System.getProperty("java.vm.version")} (${System.getProperty("java.vm.vendor")})"
            )
        }
    }

    extensions.configure<PublishingExtension> {
        publications {
            register("mavenJava", MavenPublication::class) {
                artifactId = extensions.getByType<BasePluginExtension>().archivesName.get()
                version = rootProject.version.toString()

                from(components["java"])

                pom {
                    name.set(modName)
                    inceptionYear.set("2019")

                    organization {
                        name.set("marcus8448")
                        url.set("https://github.com/marcus8448")
                    }

                    scm {
                        url.set("https://github.com/marcus8448/GamemodeOverhaul")
                        connection.set("scm:git:git://github.com/marcus8448/GamemodeOverhaul.git")
                        developerConnection.set("scm:git:git@github.com:marcus8448/GamemodeOverhaul.git")
                    }

                    issueManagement {
                        system.set("github")
                        url.set("https://github.com/marcus8448/GamemodeOverhaul/issues")
                    }

                    licenses {
                        license {
                            name.set(modLicense)
                            url.set("https://github.com/marcus8448/GamemodeOverhaul/blob/main/LICENSE")
                        }
                    }
                }
            }
        }

        repositories {
            if (System.getenv().containsKey("NEXUS_REPOSITORY_URL")) {
                maven(System.getenv("NEXUS_REPOSITORY_URL")!!) {
                    credentials {
                        username = System.getenv("NEXUS_USER")
                        password = System.getenv("NEXUS_PASSWORD")
                    }
                }
            }
        }
    }

    extensions.configure<SpotlessExtension> {
        lineEndings = com.diffplug.spotless.LineEnding.UNIX

        java {
            licenseHeaderFile(rootProject.file("LICENSE-HEADER"))
            leadingTabsToSpaces()
            removeUnusedImports()
            trimTrailingWhitespace()
        }
    }
}
