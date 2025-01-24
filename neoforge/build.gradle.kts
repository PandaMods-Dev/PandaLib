import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask

architectury {
	platformSetupLoomIde()
	neoForge()
}

loom {
	accessWidenerPath.set(project(":common").loom.accessWidenerPath)
}

configurations {
	getByName("developmentNeoForge").extendsFrom(configurations["common"])
}

dependencies {
	neoForge("net.neoforged:neoforge:${properties["neoforge_version"]}")

	modApi("dev.architectury:architectury-neoforge:${properties["deps_architectury_version"]}")
	
	common(project(":common", "namedElements")) { isTransitive = false }
	shadowBundle(project(":common", "transformProductionNeoForge"))
}

tasks.remapJar {
	injectAccessWidener = true
	atAccessWideners.add(loom.accessWidenerPath.get().asFile.name)
}

tasks.withType<RemapJarTask> {
	val shadowJar = tasks.getByName<ShadowJar>("shadowJar")
	inputFile.set(shadowJar.archiveFile)
}

publishing {
	publications {
		register("mavenJava", MavenPublication::class) {
			groupId = properties["maven_group"] as String
			artifactId = "${properties["mod_id"]}-${project.name}"
			version = "${project.version}-build.${project.findProperty("buildNumber") ?: "-1"}"

			from(components["java"])
		}
	}

	repositories {
		maven {
			name = "GitHubPackages"
			url = uri("https://maven.pkg.github.com/PandaMods-Dev/PandaLib")
			credentials {
				username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
				password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
			}
		}
	}
}