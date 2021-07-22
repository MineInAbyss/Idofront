package com.mineinabyss.idofront.slimjar;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
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
import java.util.List;

/**
 * A helper class that uses slimjar to inject dependencies into Spigot's library loader. Will also inherit all
 * classloaders from `depends` in plugin config. Loads classes into a libraries folder in plugins folder.
 *
 * Currently disables slimjar's relocation to save space, will
 */
public class LibraryLoaderInjector {
    public static void inject(Plugin plugin) throws ReflectiveOperationException, IOException, URISyntaxException, NoSuchAlgorithmException {
        PluginClassLoader classLoader = (PluginClassLoader) plugin.getClass().getClassLoader();

        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);
        Field libraryLoaderField = PluginClassLoader.class.getDeclaredField("libraryLoader");
        long libraryLoaderOffset = unsafe.objectFieldOffset(libraryLoaderField);

        List<ClassLoader> loaders = Lists.newArrayList();
        DelegateClassLoader delegateClassLoader = new DelegateClassLoader(loaders);

        URLClassLoader libraryLoader = (URLClassLoader) unsafe.getObject(classLoader, libraryLoaderOffset);
        URL[] urls;
        if (libraryLoader != null)
            urls = libraryLoader.getURLs();
        else
            urls = new URL[0];

        libraryLoader = new URLClassLoader(urls, delegateClassLoader);

        plugin.getLogger().info("Downloading dependencies...");
        Injectable injector = UnsafeInjectable.create(libraryLoader);
        ApplicationBuilder.injecting(plugin.getName(), injector)
                .downloadDirectoryPath(new File("./libraries").toPath())
                .relocatorFactory((relocationRules) -> (file1, file2) -> {})
                .relocationHelperFactory((relocator) -> (dependency, file) -> file)
                .build();


        for (String dependency : classLoader.getPlugin().getDescription().getDepend()) {
            Plugin parentPlugin = Bukkit.getPluginManager().getPlugin(dependency);
            if (parentPlugin == null) continue;

            PluginClassLoader parentClassLoader = (PluginClassLoader) parentPlugin.getClass().getClassLoader();
            ClassLoader parentLibraryLoader = (ClassLoader) unsafe.getObject(parentClassLoader, libraryLoaderOffset);
            loaders.add(parentLibraryLoader);
        }

        ImmutableList<ClassLoader> allLoaders = ImmutableList.<ClassLoader>builder()
                .add(libraryLoader)
                .addAll(loaders)
                .build();

        unsafe.putObject(classLoader, unsafe.objectFieldOffset(libraryLoaderField), new DelegateClassLoader(allLoaders));
    }
}
