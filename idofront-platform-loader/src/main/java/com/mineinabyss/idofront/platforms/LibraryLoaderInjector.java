package com.mineinabyss.idofront.platforms;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.PluginClassLoader;
import sun.misc.Unsafe;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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
        @SuppressWarnings("unchecked")
        var platformProvider = (Optional<Function<File, URLClassLoader>>) services.getKnownServices().stream()
                .filter(it -> it.getName().equals(PlatformProvider.class.getName()))
                .map(services::load)
                .findFirst();

        var platformLoader = platformProvider.orElseGet(() -> {
            var service = new PlatformProviderImpl();
            services.register(PlatformProvider.class, service, plugin, ServicePriority.Low);
            return service;
        }).apply(injectFile);

        // Update the library loader to delegate to our platform *after* the plugin's own libraries
        ClassLoader newLoader;
        if (libraryLoader == null) newLoader = platformLoader;
        else newLoader = new DelegateClassLoader(List.of(libraryLoader, platformLoader));
        Bukkit.getServicesManager();
        setLibraryClassLoaderFor(pluginClassLoader, newLoader);
    }

    @Nullable
    static ClassLoader getLibraryClassLoaderFor(ClassLoader pluginClassLoader) throws NoSuchFieldException, IllegalAccessException {
        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);
        Field libraryLoaderField = PluginClassLoader.class.getDeclaredField("libraryLoader");
        long libraryLoaderOffset = unsafe.objectFieldOffset(libraryLoaderField);

        return (ClassLoader) unsafe.getObject(pluginClassLoader, libraryLoaderOffset);
    }

    static void setLibraryClassLoaderFor(ClassLoader pluginClassLoader, ClassLoader libraryLoader) throws NoSuchFieldException, IllegalAccessException {
        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);
        Field libraryLoaderField = PluginClassLoader.class.getDeclaredField("libraryLoader");
        unsafe.putObject(pluginClassLoader, unsafe.objectFieldOffset(libraryLoaderField), libraryLoader);
    }
}
