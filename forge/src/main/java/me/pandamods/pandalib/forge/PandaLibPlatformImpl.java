package me.pandamods.pandalib.forge;

import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class PandaLibPlatformImpl {
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
