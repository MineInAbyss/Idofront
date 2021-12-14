package com.mineinabyss.idofront.slimjar;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.function.Predicate;

public class IdofrontPlatforms {
    public static void load(Plugin plugin, String platformName) throws ReflectiveOperationException {
        LibraryLoaderInjector.inject(plugin, file -> file.getName().endsWith(".platform") && file.getName().startsWith(platformName));
    }

    public static void load(Plugin plugin, Predicate<File> predicate) throws ReflectiveOperationException {
        LibraryLoaderInjector.inject(plugin, predicate);
    }
}
