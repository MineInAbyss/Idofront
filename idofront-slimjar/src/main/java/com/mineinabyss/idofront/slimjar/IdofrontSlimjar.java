package com.mineinabyss.idofront.slimjar;

import io.github.slimjar.app.builder.ApplicationBuilder;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

public class IdofrontSlimjar {
    /**
     * Loads slimjar dependencies to the global classloader.
     */
    public static void loadGlobally(Plugin plugin) throws ReflectiveOperationException, URISyntaxException, NoSuchAlgorithmException, IOException {
        plugin.getLogger().info("Downloading dependencies...");
        ApplicationBuilder.appending(plugin.getName())
                .downloadDirectoryPath(new File("./libraries").toPath())
                .relocatorFactory((relocationRules) -> (file1, file2) -> {
                })
                .relocationHelperFactory((relocator) -> (dependency, file) -> file)
                .build();
    }

    public static void loadToLibraryLoader(Plugin plugin) throws ReflectiveOperationException, IOException, URISyntaxException, NoSuchAlgorithmException {
        LibraryLoaderInjector.inject(plugin);
    }
}
