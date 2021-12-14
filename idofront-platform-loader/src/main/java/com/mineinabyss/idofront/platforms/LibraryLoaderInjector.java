package com.mineinabyss.idofront.platforms;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.PluginClassLoader;
import sun.misc.Unsafe;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * A helper class that uses reflection to inject dependencies into Spigot's library loader.
 */
public class LibraryLoaderInjector {
    /**
     * Injects a jar file into a plugin's dependencies for loading. Will ensure a shared classloader is used
     * if separate plugins inject using this method.
     */
    static void inject(Plugin plugin, File injectFile) throws ReflectiveOperationException {
        // Read library loader
        PluginClassLoader pluginClassLoader = (PluginClassLoader) plugin.getClass().getClassLoader();
        var libraryLoader = getLibraryClassLoaderFor(pluginClassLoader);

        // Get or load a service which extends the built-in java Function class, so it can be shared across classloaders
        var services = Bukkit.getServicesManager();
        if (!services.isProvidedFor(PlatformProvider.class))
            services.register(PlatformProvider.class, new PlatformProviderImpl(), plugin, ServicePriority.Low);
        @SuppressWarnings("ConstantConditions")
        var platformProvider = services.getRegistration(PlatformProvider.class).getProvider();
        var platformLoader = platformProvider.apply(injectFile);

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
