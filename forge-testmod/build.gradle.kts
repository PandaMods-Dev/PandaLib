import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask

architectury {
	platformSetupLoomIde()
	forge()
}

loom {
//	accessWidenerPath.set(project(":common").loom.accessWidenerPath)

//	forge {
//		convertAccessWideners.set(true)
//		extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)
//	}
}

configurations {
	getByName("developmentForge").extendsFrom(configurations["common"])
}

dependencies {
	forge("net.minecraftforge:forge:${properties["forge_version"]}")

	modApi("dev.architectury:architectury-forge:${properties["deps_architectury_version"]}")

	implementation(project(":forge", "namedElements")) { isTransitive = false }
	common(project(":common", "namedElements")) { isTransitive = false }
	common(project(":common-testmod", "namedElements")) { isTransitive = false }
}

tasks.remapJar {
//	injectAccessWidener = true
//	atAccessWideners.add(loom.accessWidenerPath.get().asFile.name)
}

tasks.withType<RemapJarTask> {
	val shadowJar = tasks.getByName<ShadowJar>("shadowJar")
	inputFile.set(shadowJar.archiveFile)
}