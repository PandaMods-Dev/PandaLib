import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask

architectury {
	platformSetupLoomIde()
	neoForge()
}

//loom.accessWidenerPath.set(project(":common").loom.accessWidenerPath)

configurations {
	getByName("developmentNeoForge").extendsFrom(configurations["common"])
}

dependencies {
	neoForge("net.neoforged:neoforge:${properties["neoforge_version"]}")

	modApi("dev.architectury:architectury-neoforge:${properties["deps_architectury_version"]}")

	implementation(project(":neoforge", "namedElements")) { isTransitive = false }
	common(project(":common", "namedElements")) { isTransitive = false }
	common(project(":common-testmod", "namedElements")) { isTransitive = false }
}

tasks.remapJar {
	injectAccessWidener = true
//	atAccessWideners.add(loom.accessWidenerPath.get().asFile.name)
}

tasks.withType<RemapJarTask> {
	val shadowJar = tasks.getByName<ShadowJar>("shadowJar")
	inputFile.set(shadowJar.archiveFile)
}