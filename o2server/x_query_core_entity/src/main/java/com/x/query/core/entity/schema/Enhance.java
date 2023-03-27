package com.x.query.core.entity.schema;

import java.io.File;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.openjpa.enhance.PCEnhancer;

public class Enhance {

    private static final String DOT_CLASS = ".class";

    public static void main(String... args) {

        Collection<File> files = FileUtils.listFiles(new File(args[0]), FileFilterUtils.suffixFileFilter(DOT_CLASS),
                DirectoryFileFilter.INSTANCE);
        Set<String> paths = new TreeSet<>();
        for (File f : files) {
            paths.add(f.getAbsolutePath());
        }
        PCEnhancer.main(paths.toArray(new String[] {}));
    }

}
