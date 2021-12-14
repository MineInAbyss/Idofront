package com.mineinabyss.idofront.slimjar;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

/**
 * Classloader that contains a list of loaders that will be delegated to.
 */
public class DelegateClassLoader extends URLClassLoader {
    private final Collection<? extends ClassLoader> parents;

    DelegateClassLoader(Collection<? extends ClassLoader> parents) {
        super(new URL[0]);
        this.parents = parents;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        for (ClassLoader loader: parents) {
            try {
                return loader.loadClass(name);
            } catch (NoClassDefFoundError | ClassNotFoundException ignored) {
            }
        }
        return super.loadClass(name, resolve);
    }

    @Override
    public String toString() {
        return "DelegateClassLoader{" +
                "parents=" + parents +
                '}';
    }
}
