package com.mineinabyss.idofront.slimjar;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.PluginClassLoader;
import sun.misc.Unsafe;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * A helper class that uses slimjar to inject dependencies into Spigot's library loader. Will also inherit all
 * classloaders from `depends` in plugin config. Loads classes into a libraries folder in plugins folder.
 * <p>
 * Currently disables slimjar's relocation to save space.
 */
public class LibraryLoaderInjector {
    static void inject(Plugin plugin, Predicate<File> predicate) throws ReflectiveOperationException {
        // Read library loader
        PluginClassLoader pluginClassLoader = (PluginClassLoader) plugin.getClass().getClassLoader();
        var libraryLoader = getLibraryClassLoaderFor(pluginClassLoader);

        // Find the platform file we want
        var files = plugin.getDataFolder().getParentFile().listFiles();
        if (files == null) return;
        var loadFile = Arrays.stream(files)
                .filter(predicate)
                .findFirst();
        if (loadFile.isEmpty()) return;

        // Get or load a service which extends the built-in java Function class, so it can be shared across classloaders
        var services = Bukkit.getServicesManager();
        if (!services.isProvidedFor(PlatformProvider.class))
            services.register(PlatformProvider.class, new PlatformProviderImpl(), plugin, ServicePriority.Low);
        @SuppressWarnings("ConstantConditions")
        var platformProvider = services.getRegistration(PlatformProvider.class).getProvider();
        var platformLoader = platformProvider.apply(loadFile.get());

        // Update the library loader to delegate to our platform *after* the plugin's own libraries
        var newLoader = new DelegateClassLoader(List.of(libraryLoader, platformLoader));
        Bukkit.getServicesManager();
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
