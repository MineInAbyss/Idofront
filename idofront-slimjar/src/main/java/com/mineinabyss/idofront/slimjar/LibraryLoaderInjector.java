package com.mineinabyss.idofront.slimjar;

import io.github.slimjar.app.builder.ApplicationBuilder;
import io.github.slimjar.injector.loader.Injectable;
import io.github.slimjar.injector.loader.UnsafeInjectable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.PluginClassLoader;
import sun.misc.Unsafe;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * A helper class that uses slimjar to inject dependencies into Spigot's library loader. Will also inherit all
 * classloaders from `depends` in plugin config. Loads classes into a libraries folder in plugins folder.
 * <p>
 * Currently disables slimjar's relocation to save space.
 */
public class LibraryLoaderInjector {
    static void inject(Plugin plugin)
            throws ReflectiveOperationException, IOException, URISyntaxException, NoSuchAlgorithmException {
        PluginClassLoader pluginClassLoader = (PluginClassLoader) plugin.getClass().getClassLoader();
        var libraryLoader = (URLClassLoader) getLibraryClassLoaderFor(pluginClassLoader);

        // Get parent plugins
        var desc = pluginClassLoader.getPlugin().getDescription();
        var parentPlugins = Stream.of(desc.getDepend(), desc.getSoftDepend())
                .flatMap(Collection::stream)
                .map(parent -> {
                    Plugin parentPlugin = Bukkit.getPluginManager().getPlugin(parent);
                    if(parentPlugin == null) return null;
                    try {
                        return getLibraryClassLoaderFor(parentPlugin.getClass().getClassLoader());
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        var newLoader = new DelegateClassLoader(parentPlugins);
        Injectable injector = UnsafeInjectable.create(newLoader);

        // Inject our own dependencies AFTER parents (their classes will be used instead)
        plugin.getLogger().info("Downloading dependencies...");
        ApplicationBuilder.injecting(plugin.getName(), injector)
                .downloadDirectoryPath(new File("./libraries").toPath())
                .relocatorFactory((relocationRules) -> (file1, file2) -> {
                })
                .relocationHelperFactory((relocator) -> (dependency, file) -> file)
                .build();

        setLibraryClassLoaderFor(pluginClassLoader, newLoader);
    }

    static ClassLoader getLibraryClassLoaderFor(ClassLoader pluginClassLoader) throws NoSuchFieldException, IllegalAccessException {
        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);
        Field libraryLoaderField = PluginClassLoader.class.getDeclaredField("libraryLoader");
        long libraryLoaderOffset = unsafe.objectFieldOffset(libraryLoaderField);

        ClassLoader libraryLoader = (ClassLoader) unsafe.getObject(pluginClassLoader, libraryLoaderOffset);
        if (libraryLoader == null) {
            // If null, create
            libraryLoader = new URLClassLoader(new URL[0]);
            unsafe.putObject(pluginClassLoader, unsafe.objectFieldOffset(libraryLoaderField), libraryLoader);
        }
        return libraryLoader;
    }

    static void setLibraryClassLoaderFor(ClassLoader pluginClassLoader, ClassLoader libraryLoader) throws NoSuchFieldException, IllegalAccessException {
        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);
        Field libraryLoaderField = PluginClassLoader.class.getDeclaredField("libraryLoader");
        unsafe.putObject(pluginClassLoader, unsafe.objectFieldOffset(libraryLoaderField), libraryLoader);
    }
}
