architectury {
	platformSetupLoomIde()
	forge()
}

loom {
	accessWidenerPath.set(project(":common").loom.accessWidenerPath)

	forge {
		convertAccessWideners.set(true)
		extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)

		mixinConfig("${properties["mod_id"]}-common.mixins.json")
		mixinConfig("${properties["mod_id"]}.mixins.json")
	}
}

configurations {
	getByName("developmentForge").extendsFrom(configurations["common"])
	// Required for embedding libraries into the jar because Forge is weird.
	getByName("forgeRuntimeLibrary").extendsFrom(configurations["jarShadow"])
}

dependencies {
	forge("net.minecraftforge:forge:${properties["forge_version"]}")

	modApi("dev.architectury:architectury-forge:${properties["deps_architectury_version"]}")

	common(project(":common", "namedElements")) { isTransitive = false }
	shadowBundle(project(":common", "transformProductionForge"))
}

tasks.remapJar {
	injectAccessWidener = true
	atAccessWideners.add(loom.accessWidenerPath.get().asFile.name)
}