package com.x.server.console.action;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;

public class EntityClassLoaderTools {

    private EntityClassLoaderTools() {
        // nothing
    }

    public static ClassLoader concreteClassLoader() throws IOException, URISyntaxException {
        List<URL> urlList = new ArrayList<>();
        IOFileFilter filter = new WildcardFileFilter("*.jar");
        for (File o : FileUtils.listFiles(Config.dir_dynamic_jars(true), filter, null)) {
            urlList.add(o.toURI().toURL());
        }
        for (File o : FileUtils.listFiles(Config.dir_custom_jars(true), filter, null)) {
            urlList.add(o.toURI().toURL());
        }
        for (File o : FileUtils.listFiles(Config.dir_store_jars(true), filter, null)) {
            if (!StringUtils.equalsIgnoreCase(FilenameUtils.getBaseName(o.toString()), "x_base_core_project")) {
                urlList.add(o.toURI().toURL());
            }
        }
        urlList.add(Config.dir_local_temp_classes().toURI().toURL());
        URL[] urls = new URL[urlList.size()];
        return URLClassLoader.newInstance(urlList.toArray(urls), ClassLoader.getSystemClassLoader());
    }

}
