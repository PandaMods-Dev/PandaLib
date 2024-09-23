import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.RemapJarTask

plugins {
	java

	id("architectury-plugin") version "3.4-SNAPSHOT"
	id("dev.architectury.loom") version "1.7-SNAPSHOT" apply false

	id("com.github.johnrengelman.shadow") version "8.1.1" apply false
	id("systems.manifold.manifold-gradle-plugin") version "0.0.2-alpha"

	id("maven-publish")
	id("me.modmuss50.mod-publish-plugin") version "0.6.3"
}

/**
 * Borrowed from Distant Horizons
 */
fun writeBuildGradlePredefine(AvailableVersion: List<String>, versionIndex: Int) {
	val sb = StringBuilder()

	sb.append("# DON'T TOUCH THIS FILE, This is handled by the build script\n")

	for ((index, s) in AvailableVersion.withIndex()) {
		val versionString = s.replace(".", "_")
		sb.append("MC_${versionString}=${index}\n")
		ext.set("MC_${versionString}", index.toString())

		if (versionIndex == index) {
			sb.append("MC_VER=${index}\n")
			ext.set("MC_VER", index.toString())
		}
	}

	File(projectDir, "build.properties").writeText(sb.toString())
}

project.gradle.extra.properties.forEach { prop ->
	ext.set(prop.key, prop.value)
}

writeBuildGradlePredefine(properties["available_versions"] as List<String>, properties["version_index"] as Int)

architectury.minecraft = properties["minecraft_version"] as String

allprojects {
	apply(plugin = "java")

	base { archivesName = properties["mod_id"] as String }
	version = "${properties["mod_version"]}-${properties["minecraft_version"]}"
	group = properties["maven_group"] as String
}

subprojects {
	val isMinecraftSubProject = findProject(":common") != project && findProject(":testmod-common") != project
	val isFabric = findProject(":fabric") == project || findProject(":testmod-fabric") == project
	val isForge = findProject(":forge") == project || findProject(":testmod-forge") == project
	val isNeoForge = findProject(":neoforge") == project || findProject(":testmod-neoforge") == project

	apply(plugin = "architectury-plugin")
	apply(plugin = "dev.architectury.loom")

	apply(plugin = "maven-publish")
	apply(plugin = "com.github.johnrengelman.shadow")

	base { archivesName = "${properties["mod_id"]}-${project.name}" }

	val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")
	loom.silentMojangMappingsLicense()
	if (isMinecraftSubProject) {
		loom.runs {
			named("client") {
				client()
				configName = "Client"
				ideConfigGenerated(true)
				runDir("../.runs/client")
				source(sourceSets["main"])
				programArgs("--username=Dev")
			}
			named("server") {
				server()
				configName = "Server"
				ideConfigGenerated(true)
				runDir("../.runs/server")
				source(sourceSets["main"])
			}
		}
	}

	configurations {
		create("common") {
			isCanBeResolved = true
			isCanBeConsumed = false
		}
		compileClasspath.get().extendsFrom(configurations["common"])
		runtimeClasspath.get().extendsFrom(configurations["common"])

		// Files in this configuration will be bundled into your mod using the Shadow plugin.
		// Don't use the `shadow` configuration from the plugin itself as it's meant for excluding files.
		create("shadowBundle") {
			isCanBeResolved = true
			isCanBeConsumed = false
		}

		create("jarShadow") {
			isCanBeResolved = true
			isCanBeConsumed = false
		}
		implementation.get().extendsFrom(configurations["jarShadow"])

		create("modShadow")
		getByName("modImplementation").extendsFrom(configurations["modShadow"])
		getByName("include").extendsFrom(configurations["modShadow"])
	}

	repositories {
		mavenCentral()
		mavenLocal()

		maven("https://maven.parchmentmc.org")
		maven("https://maven.fabricmc.net/")
		maven("https://maven.minecraftforge.net/")
		maven("https://maven.neoforged.net/releases/")
	}

	@Suppress("UnstableApiUsage")
	dependencies {
		"minecraft"("com.mojang:minecraft:${properties["minecraft_version"]}") {
			exclude(group = "org.joml", module = "joml")
		}
		"mappings"(loom.layered {
			officialMojangMappings()
			parchment("org.parchmentmc.data:parchment-${properties["parchment_minecraft_version"]}:${properties["parchment_version"]}@zip")
		})

		// Assimp Library
		"jarShadow"("org.lwjgl:lwjgl-assimp:${properties["deps_lwjgl_version"]}") {
			exclude(group = "org.lwjgl", module = "lwjgl")
		}
		if (isMinecraftSubProject) {
			// Assimp natives
			"jarShadow"("org.lwjgl:lwjgl-assimp:${properties["deps_lwjgl_version"]}:natives-windows") {
				exclude(group = "org.lwjgl", module = "lwjgl")
			}

			"jarShadow"("org.lwjgl:lwjgl-assimp:${properties["deps_lwjgl_version"]}:natives-linux") {
				exclude(group = "org.lwjgl", module = "lwjgl")
			}

			"jarShadow"("org.lwjgl:lwjgl-assimp:${properties["deps_lwjgl_version"]}:natives-macos") {
				exclude(group = "org.lwjgl", module = "lwjgl")
			}
		}

		// Embed joml
		"jarShadow"("org.joml:joml:${properties["deps_joml_version"]}")

		compileOnly("org.jetbrains:annotations:24.1.0")
		annotationProcessor("systems.manifold:manifold-preprocessor:${properties["deps_manifold_version"]}")
	}

	if (isMinecraftSubProject) {
		tasks.withType<ShadowJar> {
			configurations = listOf(project.configurations.getByName("shadowBundle"), project.configurations.getByName("jarShadow"))
			archiveClassifier.set("dev-shadow")

			exclude("architectury.common.json")
		}

		tasks.withType<RemapJarTask> {
			val shadowJar = tasks.getByName<ShadowJar>("shadowJar")
			inputFile.set(shadowJar.archiveFile)
		}
	}

	tasks.withType<ShadowJar> {
		// Relocate assimp so it will not cause any conflicts with other mods also using it.
		relocate("org.lwjgl.assimp", "${properties["maven_group"]}.assimp")
		// Relocate natives
		relocate("windows.x64.org.lwjgl.assimp", "windows.x64.${properties["maven_group"]}.assimp")
		relocate("linux.x64.org.lwjgl.assimp", "linux.x64.${properties["maven_group"]}.assimp")
		relocate("macos.x64.org.lwjgl.assimp", "macos.x64.${properties["maven_group"]}.assimp")

		relocate("META-INF.windows.arm64.org.lwjgl.assimp", "META-INF.windows.arm64.${properties["maven_group"]}.assimp")
		relocate("META-INF.windows.x64.org.lwjgl.assimp", "META-INF.windows.x64.${properties["maven_group"]}.assimp")
		relocate("META-INF.windows.x86.org.lwjgl.assimp", "META-INF.windows.x86.${properties["maven_group"]}.assimp")

		relocate("META-INF.linux.arm32.org.lwjgl.assimp", "META-INF.linux.arm32.${properties["maven_group"]}.assimp")
		relocate("META-INF.linux.arm64.org.lwjgl.assimp", "META-INF.linux.arm64.${properties["maven_group"]}.assimp")
		relocate("META-INF.linux.x64.org.lwjgl.assimp", "META-INF.linux.x64.${properties["maven_group"]}.assimp")

		relocate("META-INF.macos.arm64.org.lwjgl.assimp", "META-INF.macos.arm64.${properties["maven_group"]}.assimp")
		relocate("META-INF.macos.x64.org.lwjgl.assimp", "META-INF.macos.x64.${properties["maven_group"]}.assimp")

		// Relocate joml as to not cause issues with Minecraft
		relocate("org.joml", "${properties["maven_group"]}.joml")
	}

	tasks.withType<JavaCompile> {
		options.encoding = "UTF-8"
		options.release.set(JavaLanguageVersion.of(properties["java_version"] as String).asInt())
		options.compilerArgs.add("-Xplugin:Manifold")
	}

	tasks.processResources {
		val props = mutableMapOf(
			"java_version" to properties["java_version"],
			"supported_mod_loaders" to properties["supported_mod_loaders"],

			"maven_group" to properties["maven_group"],
			"mod_id" to properties["mod_id"],
			"mod_version" to properties["mod_version"],
			"mod_name" to properties["mod_name"],
			"mod_description" to properties["mod_description"],
			"mod_author" to properties["mod_author"],
			"mod_license" to properties["mod_license"],

			"project_curseforge_slug" to properties["project_curseforge_slug"],
			"project_modrinth_slug" to properties["project_modrinth_slug"],
			"project_github_repo" to properties["project_github_repo"],
		)

		if (properties["fabric_version_range"] != null)
			props["fabric_version_range"] = properties["fabric_version_range"] as String

		if (properties["forge_version_range"] != null)
			props["forge_version_range"] = properties["forge_version_range"] as String

		if (properties["neoforge_version_range"] != null)
			props["neoforge_version_range"] = properties["neoforge_version_range"] as String

		inputs.properties(props)
		filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "META-INF/mods.toml", "META-INF/neoforge.mods.toml", "*.mixins.json")) {
			expand(props)
		}
	}

	tasks.jar {
		manifest {
			attributes(mapOf(
					"Specification-Title" to properties["mod_name"],
					"Specification-Vendor" to properties["mod_author"],
					"Specification-Version" to properties["mod_version"],
					"Implementation-Title" to name,
					"Implementation-Vendor" to properties["mod_author"],
					"Implementation-Version" to archiveVersion
			))
		}
	}

	java {
		withSourcesJar()
	}

	// Maven Publishing
	publishing {
		publications {
			create<MavenPublication>("mod") {
				groupId = properties["maven_group"] as String
				artifactId = "${{properties["mod_id"]}}-${project.name}"
				version = project.version as String

				from(components["java"])
			}
		}

		repositories {
		}
	}
}

tasks.register("publishLocallyAll") {
	val availableVersions = properties["availableVersions"] as List<String>

	availableVersions.forEach { version ->
		doLast {
			exec {
				commandLine = listOf("gradlew.bat", "-PminecraftVersion=$version", "publishToMavenLocal")
			}
		}
	}
}

// Mod Publishing
var curseForgeAPIKey = providers.environmentVariable("CURSEFORGE_API_KEY")
var modrinthAPIKey = providers.environmentVariable("MODRINTH_API_KEY")
var githubAPIKey = providers.environmentVariable("GITHUB_API_KEY")

publishMods {
	dryRun = properties["publishing_dry_run"].toString().toBoolean()

	version = properties["mod_version"] as String
	changelog = rootProject.file("CHANGELOG.md").readText()

	// Set the release type
	type = when (properties["publishing_release_type"].toString().toInt()) {
		2 -> ALPHA
		1 -> BETA
		else -> STABLE
	}

	val isRangedVersion = properties["publishing_latest_minecraft_version"] != null
	val minecraftVersionStr = if (isRangedVersion) {
		"${properties["publishing_minecraft_version"]}-${properties["publishing_latest_minecraft_version"]}"
	} else {
		properties["publishing_minecraft_version"]
	}

	// Creates publish options for each supported mod loader
	properties["supported_mod_loaders"].toString().split(",").forEach {
		val loaderName = it
		val loaderDisplayName = when (it) {
			"fabric" -> "Fabric"
			"forge" -> "Forge"
			"neoforge" -> "NeoForge"
			else -> it
		}

		val remapJar = rootProject.project(":" + loaderName).tasks.getByName<RemapJarTask>("remapJar")

		curseforge("curseforge_${loaderName}") {
			accessToken = curseForgeAPIKey
			displayName = "[${loaderDisplayName} ${minecraftVersionStr}] v${properties["mod_version"]}"

			projectId = properties["project_curseforge_id"] as String

			modLoaders.add(loaderName)
			file = remapJar.archiveFile

			if (isRangedVersion)
				minecraftVersionRange {
					start = properties["publishing_minecraft_version"] as String
					end = properties["publishing_latest_minecraft_version"] as String
				}
			else
				minecraftVersions.add(properties["publishing_minecraft_version"] as String)

			javaVersions.add(JavaVersion.VERSION_21)

			clientRequired = true
			serverRequired = true

			if (loaderName == "fabric")
				requires("fabric-api")
		}

		modrinth("modrinth_" + loaderName) {
			accessToken = modrinthAPIKey
			displayName = "[${loaderDisplayName} ${minecraftVersionStr}] v${properties["mod_version"]}"

			projectId = properties["project_modrinth_id"] as String

			modLoaders.add(loaderName)
			file = remapJar.archiveFile

			if (isRangedVersion)
				minecraftVersionRange {
					start = properties["publishing_minecraft_version"] as String
					end = properties["publishing_latest_minecraft_version"] as String
				}
			else
				minecraftVersions.add(properties["publishing_minecraft_version"] as String)

			if (loaderName == "fabric")
				requires("fabric-api")
		}
	}

	val githubRepository = properties["project_github_repo"] as String
	val releaseType = when (properties["publishing_release_type"].toString().toInt()) {
		2 -> "alpha"
		1 -> "beta"
		else -> "stable"
	}
	val githubTagName = "${releaseType}/${properties["mod_version"]}-${minecraftVersionStr}"
	github {
		displayName = "${properties["mod_name"]} ${properties["mod_version"]} MC${minecraftVersionStr}"
		accessToken = githubAPIKey
		repository = githubRepository
		tagName = githubTagName
		commitish = "main"

		modLoaders.addAll(properties["supported_mod_loaders"].toString().trim().split(","))
		val commonRemapJar = project(":common").tasks.getByName<RemapJarTask>("remapJar")
		file = commonRemapJar.archiveFile

		properties["supported_mod_loaders"].toString().split(",").forEach {
			val modRemapJar = project(":$it").tasks.getByName<RemapJarTask>("remapJar")
			additionalFiles.from(modRemapJar.archiveFile)
		}
	}
}
