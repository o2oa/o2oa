package com.x.base.core.project.tools;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class URLClassLoaderTools {

    private URLClassLoaderTools() {
    }

    public static void add(URLClassLoader classLoader, File file) throws Exception {
        Class<?> urlClass = URLClassLoader.class;
        Method method = urlClass.getDeclaredMethod("addURL", new Class[] { URL.class });
        method.setAccessible(true);
        method.invoke(classLoader, new Object[] { file.toURI().toURL() });
    }

}