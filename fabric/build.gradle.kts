import com.modrinth.minotaur.ModrinthExtension
import net.darkhax.curseforgegradle.TaskPublishCurseForge
import org.gradle.kotlin.dsl.configure

val modId = project.property("mod.id").toString()
val modName = project.property("mod.name").toString()
val modVersion = project.property("mod.version").toString()
val minecraft = project.property("minecraft.version").toString()
val fabricLoader = project.property("fabric.loader.version").toString()
val fabricAPI = project.property("fabric.api.version").toString()
val fabricModules = project.property("fabric.api.modules").toString().split(',')
val modmenu = project.property("modmenu.version")
val clothConfig = project.property("cloth.config.version")
val curseforgeId = (project.property("mod.curseforge.id") ?: "").toString()
val modrinthId = (project.property("mod.modrinth.id") ?: "").toString()

plugins {
    id("net.fabricmc.fabric-loom")
    id("com.modrinth.minotaur")
    id("net.darkhax.curseforgegradle")
}

loom {
    // configure access widener
    if (project(":fabric").file("src/main/resources/${modId}.accesswidener").exists()) {
        accessWidenerPath.set(project(":fabric").file("src/main/resources/${modId}.accesswidener"))
    }

    // disable Minecraft-altering loom features, so that we can have one less copy of Minecraft
    interfaceInjection.enableDependencyInterfaceInjection.set(false)
    interfaceInjection.getIsEnabled().set(false)
    enableTransitiveAccessWideners.set(false)

    runs {
        named("client") {
            client()
            name("Client")
        }
        named("server") {
            server()
            name("Server")
        }
        create("gametest") {
            server()
            name("GameTest")
            property("fabric-api.gametest")
            vmArgs("-ea")
        }

        configureEach {
            runDir("run")
            ideConfigGenerated(true)
        }
    }
}

repositories {
    maven("https://maven.terraformersmc.com/releases/") {
        content {
            includeGroup("com.terraformersmc")
        }
    }
    maven("https://maven.shedaniel.me/") {
        content {
            includeGroup("me.shedaniel.cloth.api")
            includeGroup("me.shedaniel.cloth")
            includeGroup("me.shedaniel")
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    implementation("net.fabricmc:fabric-loader:$fabricLoader")
    compileOnly(project(":common"))

    fabricModules.forEach {
        implementation(fabricApi.module(it, fabricAPI))
    }
    implementation("com.terraformersmc:modmenu:${modmenu}") { isTransitive = false }
    implementation("me.shedaniel.cloth:cloth-config-fabric:${clothConfig}") {
        exclude(group = "net.fabricmc")
        exclude(group = "net.fabricmc.fabric-api")
    }

    runtimeOnly("net.fabricmc.fabric-api:fabric-api:$fabricAPI")
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

tasks.validateAccessWidener {
    enabled = false // access widener validated by :common
}

modrinth {
    dependencies {
        required.project("fabric-api")
    }
}

tasks.register<TaskPublishCurseForge>("curseforge") {
    apiToken = System.getenv("CURSEFORGE_TOKEN").toString()
    val mainFile = upload(curseforgeId, tasks.findByName("remapJar") ?: tasks.getByName("jar"))
    mainFile.addGameVersion(project.name)
    mainFile.addGameVersion(minecraft)
    mainFile.displayName = "$modName $modVersion (${project.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }} ${minecraft})"
    mainFile.addRequirement("fabric-api")

    if (System.getenv().containsKey("CHANGELOG")) {
        mainFile.changelog = System.getenv("CHANGELOG").toString()
    } else {
        mainFile.changelog = "No changelog provided."
    }
}

extensions.configure<ModrinthExtension> {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set(modrinthId)
    uploadFile.set(tasks.findByName("remapJar") ?: tasks.getByName("jar"))
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
