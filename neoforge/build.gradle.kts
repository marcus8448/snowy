import com.modrinth.minotaur.ModrinthExtension
import net.darkhax.curseforgegradle.TaskPublishCurseForge
import org.gradle.kotlin.dsl.configure

val minecraft = project.property("minecraft.version").toString()
val modName = project.property("mod.name").toString()
val modVersion = project.property("mod.version").toString()
val neoforge = project.property("neoforge.version").toString()
val curseforgeId = (project.property("mod.curseforge.id") ?: "").toString()
val modrinthId = (project.property("mod.modrinth.id") ?: "").toString()

plugins {
    `java-library`
    `maven-publish`
    idea
    id("net.neoforged.moddev")
    id("com.modrinth.minotaur")
    id("net.darkhax.curseforgegradle")
}

neoForge {
    // Specify the version of NeoForge to use.
    version = neoforge

    runs {
        register("client") {
            client()
        }

        register("server") {
            server()
            programArgument("--nogui")
        }

        register("gameTest") {
            type = "gameTestServer"
        }

        configureEach {
            ideName = "${name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }} (${project.path})"
            // minecraft because forge patches @GameTest for the filtering... and common cannot implement the patch
            systemProperty("neoforge.enabledGameTestNamespaces", "minecraft")
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.DEBUG
        }
    }
    

    mods {
        register("modId") {
            sourceSet(sourceSets.main.get())
        }
    }
}

dependencies {
    compileOnly(project(":common"))
}

tasks.compileJava {
    source(project(":common").sourceSets.main.get().java)
}

tasks.processResources {
    from(project(":common").sourceSets.main.get().resources)
}

tasks.javadoc {
    source(project(":common").sourceSets.main.get().allJava)
}

tasks.register<TaskPublishCurseForge>("curseforge") {
    apiToken = System.getenv("CURSEFORGE_TOKEN").toString()
    val mainFile = upload(curseforgeId, tasks.getByName("jar"))
    mainFile.addGameVersion(project.name)
    mainFile.addGameVersion(minecraft)
    mainFile.displayName = "$modName $modVersion (${project.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }} ${minecraft})"

    if (System.getenv().containsKey("CHANGELOG")) {
        mainFile.changelog = System.getenv("CHANGELOG").toString()
    } else {
        mainFile.changelog = "No changelog provided."
    }
}

extensions.configure<ModrinthExtension> {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set(modrinthId)
    uploadFile.set(tasks.getByName("jar"))
    versionNumber.set("${modVersion}+${minecraft}-${project.name}")
    versionName.set("$modName v${modVersion} (${project.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }} ${minecraft})")
    versionType.set("release")

    gameVersions.addAll(minecraft)
    loaders.add(project.name)
    syncBodyFrom.set(rootProject.file("README.md").readText())

    if (System.getenv().containsKey("CHANGELOG")) {
        changelog.set(System.getenv("CHANGELOG").toString())
    }
}
