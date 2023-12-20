package me.pandalib.forge;

import me.pandalib.PandaLibExpectPlatform;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class PandaLibExpectPlatformImpl {
    /**
     * This is our actual method to {@link PandaLibExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
