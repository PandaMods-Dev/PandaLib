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

writeBuildGradlePredefine(project.properties["availableVersions"] as List<String>, project.properties["versionIndex"] as Int)

// gradle.properties
val supportedModLoaders: String by project

val projectArchivesName: String by project
val projectGroup: String by project

val modId: String by project
val modVersion: String by project
val projectJavaVersion: String by project
val modName: String by project
val modDescription: String by project
val modAuthor: String by project

val minecraftVersion: String by project

val parchmentVersion: String by project
val parchmentMinecraftVersion: String by project

val lwjglVersion: String by project
val jomlVersion = properties["jomlVersion"]
val manifoldVersion: String by project

val architecturyVersion: String by project

val fabricCompatibleVersions: String by project
val forgeCompatibleVersions: String by project
val neoForgeCompatibleVersions: String by project

architectury.minecraft = minecraftVersion

allprojects {
	apply(plugin = "java")

	tasks { base.archivesName = projectArchivesName }
	version = "${modVersion}-${minecraftVersion}"
	group = projectGroup
}

subprojects {
	val isMinecraftSubProject = findProject(":common") != project
	val isFabric = findProject(":fabric") == project
	val isForge = findProject(":forge") == project
	val isNeoForge = findProject(":neoforge") == project

	apply(plugin = "architectury-plugin")
	apply(plugin = "dev.architectury.loom")

	apply(plugin = "maven-publish")
	apply(plugin = "com.github.johnrengelman.shadow")

	tasks { base.archivesName = "${projectArchivesName}-${project.name}" }

	val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")
	loom.silentMojangMappingsLicense()

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

		create("jarShadow")
		implementation.get().extendsFrom(configurations["jarShadow"])
		getByName("shadowBundle").extendsFrom(configurations["jarShadow"])

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
		"minecraft"("com.mojang:minecraft:${minecraftVersion}")
		"mappings"(loom.layered {
			officialMojangMappings()
			parchment("org.parchmentmc.data:parchment-${parchmentMinecraftVersion}:${parchmentVersion}@zip")
		})

		if (isMinecraftSubProject) {
			"modApi"("dev.architectury:architectury-${project.name}:${architecturyVersion}")
		} else {
			"modApi"("dev.architectury:architectury:${architecturyVersion}")
		}

		// Assimp Library
		"jarShadow"("org.lwjgl:lwjgl-assimp:${lwjglVersion}") {
			exclude(group = "org.lwjgl", module = "lwjgl")
		}
		if (isMinecraftSubProject) {
			// Assimp natives
			"jarShadow"("org.lwjgl:lwjgl-assimp:${lwjglVersion}:natives-windows") {
				exclude(group = "org.lwjgl", module = "lwjgl")
			}

			"jarShadow"("org.lwjgl:lwjgl-assimp:${lwjglVersion}:natives-linux") {
				exclude(group = "org.lwjgl", module = "lwjgl")
			}

			"jarShadow"("org.lwjgl:lwjgl-assimp:${lwjglVersion}:natives-macos") {
				exclude(group = "org.lwjgl", module = "lwjgl")
			}
		}

		if (jomlVersion != null) {
			"jarShadow"("org.joml:joml:${jomlVersion}")
		}

		compileOnly("org.jetbrains:annotations:24.1.0")
		annotationProcessor("systems.manifold:manifold-preprocessor:${manifoldVersion}")
	}

	if (isMinecraftSubProject) {
		tasks.withType<ShadowJar>().configureEach {
			configurations = listOf(project.configurations.getByName("shadowBundle"))
			archiveClassifier.set("dev-shadow")

			exclude("architectury.common.json")

			// Relocate assimp so it will not cause any conflicts with other mods also using it.
			relocate("org.lwjgl.assimp", "$projectGroup.assimp")
			// Relocate natives
			relocate("windows.x64.org.lwjgl.assimp", "windows.x64.$projectGroup.assimp")
			relocate("linux.x64.org.lwjgl.assimp", "linux.x64.$projectGroup.assimp")
			relocate("macos.x64.org.lwjgl.assimp", "macos.x64.$projectGroup.assimp")

			relocate("META-INF.windows.arm64.org.lwjgl.assimp", "META-INF.windows.arm64.$projectGroup.assimp")
			relocate("META-INF.windows.x64.org.lwjgl.assimp", "META-INF.windows.x64.$projectGroup.assimp")
			relocate("META-INF.windows.x86.org.lwjgl.assimp", "META-INF.windows.x86.$projectGroup.assimp")

			relocate("META-INF.linux.arm32.org.lwjgl.assimp", "META-INF.linux.arm32.$projectGroup.assimp")
			relocate("META-INF.linux.arm64.org.lwjgl.assimp", "META-INF.linux.arm64.$projectGroup.assimp")
			relocate("META-INF.linux.x64.org.lwjgl.assimp", "META-INF.linux.x64.$projectGroup.assimp")

			relocate("META-INF.macos.arm64.org.lwjgl.assimp", "META-INF.macos.arm64.$projectGroup.assimp")
			relocate("META-INF.macos.x64.org.lwjgl.assimp", "META-INF.macos.x64.$projectGroup.assimp")

			if (jomlVersion != null)
				relocate("org.joml", "$projectGroup.joml")
		}

		tasks.withType<RemapJarTask>().configureEach {
			val shadowJar = tasks.getByName<ShadowJar>("shadowJar")
			inputFile.set(shadowJar.archiveFile)
		}
	}

	tasks.withType<JavaCompile>().configureEach {
		options.encoding = "UTF-8"
		options.release.set(JavaLanguageVersion.of(projectJavaVersion).asInt())
		options.compilerArgs.add("-Xplugin:Manifold")
	}

	tasks.processResources {
		val properties = mapOf(
				"minecraftVersion" to minecraftVersion,

				"modId" to modId,
				"modVersion" to modVersion,
				"modName" to modName,
				"modDescription" to modDescription,
				"modAuthor" to modAuthor,
		).toMutableMap()

		if (isFabric) properties["fabricCompatibleVersions"] = fabricCompatibleVersions
		if (isForge) properties["forgeCompatibleVersions"] = forgeCompatibleVersions
		if (isNeoForge) properties["neoForgeCompatibleVersions"] = neoForgeCompatibleVersions

		inputs.properties(properties)
		filesMatching(listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml", "pack.mcmeta", "fabric.mod.json")) {
			expand(properties)
		}
	}

	tasks.jar {
		manifest {
			attributes(mapOf(
					"Specification-Title" to modName,
					"Specification-Vendor" to modAuthor,
					"Specification-Version" to modVersion,
					"Implementation-Title" to name,
					"Implementation-Vendor" to modAuthor,
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
				groupId = projectGroup
				artifactId = "${projectArchivesName}-${project.name}"
				version = project.version as String

				from(components["java"])
			}
		}

		repositories {
		}
	}
}

// Mod Publishing
val publishingDryRun: String by project
val publishingReleaseType: String by project

val publishingMinecraftVersion: String by project
val publishingLatestMinecraftVersion = properties["publishingLatestMinecraftVersion"]

val publishingCurseForgeProjectId: String by project
val publishingModrinthProjectId: String by project

val publishingGitHubRepo: String by project

var curseForgeAPIKey = providers.environmentVariable("CURSEFORGE_API_KEY")
var modrinthAPIKey = providers.environmentVariable("MODRINTH_API_KEY")
var githubAPIKey = providers.environmentVariable("GITHUB_API_KEY")

publishMods {
	dryRun = publishingDryRun.toBoolean()

	version = modVersion
	changelog = rootProject.file("CHANGELOG.md").readText()

	// Set the release type
	type = when (publishingReleaseType.toInt()) {
		2 -> ALPHA
		1 -> BETA
		else -> STABLE
	}

	val isRangedVersion = publishingLatestMinecraftVersion != null
	val minecraftVersionStr = if (isRangedVersion) {
		"${publishingMinecraftVersion}-${publishingLatestMinecraftVersion}"
	} else {
		publishingMinecraftVersion
	}

	// Creates publish options for each supported mod loader
	supportedModLoaders.toString().split(",").forEach {
		val loaderName = it
		val loaderDisplayName = when (it) {
			"fabric" -> "Fabric"
			"forge" -> "Forge"
			"neoforge" -> "NeoForge"
			else -> it
		}

		val remapJar = project(":" + loaderName).tasks.getByName<RemapJarTask>("remapJar")

		curseforge("curseforge_${loaderName}") {
			accessToken = curseForgeAPIKey
			displayName = "[${loaderDisplayName} ${minecraftVersionStr}] v${modVersion}"

			projectId = publishingCurseForgeProjectId

			modLoaders.add(loaderName)
			file = remapJar.archiveFile

			if (isRangedVersion)
				minecraftVersionRange {
					start = publishingMinecraftVersion
					end = publishingLatestMinecraftVersion as String
				}
			else
				minecraftVersions.add(publishingMinecraftVersion)

			javaVersions.add(JavaVersion.VERSION_21)

			clientRequired = true
			serverRequired = true

			if (loaderName == "fabric")
				requires("fabric-api")

			requires("architectury-api")
		}

		modrinth("modrinth_" + loaderName) {
			accessToken = modrinthAPIKey
			displayName = "[${loaderDisplayName} ${minecraftVersionStr}] v${modVersion}"

			projectId = publishingModrinthProjectId

			modLoaders.add(loaderName)
			file = remapJar.archiveFile

			if (isRangedVersion)
				minecraftVersionRange {
					start = publishingMinecraftVersion
					end = publishingLatestMinecraftVersion as String
				}
			else
				minecraftVersions.add(publishingMinecraftVersion)

			if (loaderName == "fabric")
				requires("fabric-api")

			requires("architectury-api")
		}
	}

	var githubRepository = publishingGitHubRepo
	var releaseType = when (publishingReleaseType.toInt()) {
		2 -> "alpha"
		1 -> "beta"
		else -> "stable"
	}
	var githubTagName = "${releaseType}/${modVersion}-${minecraftVersionStr}"
	github {
		displayName = "${modName} ${modVersion} MC${minecraftVersionStr}"
		accessToken = githubAPIKey
		repository = githubRepository
		tagName = githubTagName
		commitish = "main"

		modLoaders.addAll(supportedModLoaders.trim().split(","))
		val commonRemapJar = project(":common").tasks.getByName<RemapJarTask>("remapJar")
		file = commonRemapJar.archiveFile

		supportedModLoaders.toString().split(",").forEach {
			val modRemapJar = project(":$it").tasks.getByName<RemapJarTask>("remapJar")
			additionalFiles.from(modRemapJar.archiveFile)
		}
	}
}
