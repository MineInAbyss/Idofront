package com.mineinabyss.idofront.loading;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;

public class IdofrontPluginLoader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        var logger = classpathBuilder.getContext().getLogger();
        var plugin = classpathBuilder.getContext().getConfiguration().getName();
        logger.info("Loading idofront dependencies");
        InputStream resource;

        try (var stream = Files.find(classpathBuilder.getContext().getDataDirectory().getParent(), 1, (path, attr) ->
                path.getFileName().toString().startsWith("idofront"))) {
            var first = stream.findFirst();
            if (first.isEmpty()) {
                logger.error("This plugin requires idofront to be installed. Please install it from https://github.com/MineInAbyss/Idofront/releases/");
                return;
            }
            resource = new URLClassLoader(new URL[]{first.get().toUri().toURL()})
                    .getResourceAsStream("maven-deps.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (resource == null) {
            logger.error("Could not find maven-deps.txt in idofront jar, not including them in classpath");
            return;
        }
        var resolver = new MavenLibraryResolver();
        try (var reader = new BufferedReader(new InputStreamReader(resource)).lines()) {
            reader.forEach(line -> resolver.addDependency(new Dependency(new DefaultArtifact(line), null)));
        }

        resolver.addRepository(new RemoteRepository.Builder("central", "default", "https://repo.maven.apache.org/maven2").build());

        classpathBuilder.addLibrary(resolver);
    }
}
