package com.mineinabyss.idofront.slimjar;

import java.util.Collection;

/**
 * Classloader that contains a list of loaders that will be delegated to.
 */
public class DelegateClassLoader extends ClassLoader {
    private final Collection<? extends ClassLoader> loaders;

    DelegateClassLoader(Collection<? extends ClassLoader> loaders) {
        this.loaders = loaders;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        for (ClassLoader loader: loaders) {
            try {
                return loader.loadClass(name);
            } catch (ClassNotFoundException ignored) {
            }
        }
        throw new ClassNotFoundException(name);
    }
}
