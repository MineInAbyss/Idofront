package com.mineinabyss.idofront.platforms;

import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.java.PluginClassLoader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

interface PlatformProvider extends Function<File, URLClassLoader> {
}

class PlatformProviderImpl implements PlatformProvider {
    private final Map<File, URLClassLoader> alreadyLoaded = new HashMap<>();

    @Override
    public URLClassLoader apply(File file) {
        var cached = alreadyLoaded.get(file);
        if (cached == null) {
            try {
                cached = new URLClassLoader(new URL[]{file.toURI().toURL()}, JavaPluginLoader.class.getClassLoader());
                alreadyLoaded.put(file, cached);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return cached;
    }
}
