package com.x.base.core.project.tools;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;

public class ClassLoaderTools {

	private ClassLoaderTools() {
		// nothing
	}

	private static final String CFG = "manifest.cfg";
	private static final String GIT = ".gitignore";
	private static final String JAR = ".jar";
	private static final String ZIP = ".zip";

	public static URLClassLoader urlClassLoader(ClassLoader parent, boolean ext, boolean store, boolean custom,
			boolean dynamic, Path... paths) throws Exception {
		return urlClassLoader(parent, ext, store, custom, dynamic, ListTools.toList(paths));
	}

	public static URLClassLoader urlClassLoader(ClassLoader parent, boolean ext, boolean store, boolean custom,
			boolean dynamic, List<Path> paths) throws Exception {
		Set<Path> set = new HashSet<>();
		if (ext) {
			set.addAll(dirCfg(Config.pathCommonsExt(true)));
		}
		if (store) {
			set.addAll(dirCfg(Config.dir_store_jars().toPath()));
		}
		if (custom) {
			set.addAll(dir(Config.dir_custom_jars().toPath()));
		}
		if (dynamic) {
			set.addAll(dir(Config.dir_dynamic_jars().toPath()));
		}
		set.addAll(paths);
		if (null != parent) {
			return URLClassLoader.newInstance(toURL(set), parent);
		} else {
			return URLClassLoader.newInstance(toURL(set));
		}
	}

	private static URL[] toURL(Set<Path> set) throws MalformedURLException {
		URL[] urls = new URL[set.size()];
		int idx = 0;
		for (Path p : set) {
			urls[idx++] = p.toUri().toURL();
		}
		return urls;
	}

	private static List<Path> dirCfg(Path dir) throws IOException {
		List<Path> paths = new ArrayList<>();
		Path cfg = dir.resolve(CFG);
		if (Files.exists(dir) && Files.isDirectory(dir) && Files.exists(cfg) && Files.isRegularFile(cfg)) {
			List<String> names = Files.readAllLines(cfg);
			try (Stream<Path> stream = Files.list(dir)) {
				stream.filter(o -> !(o.getFileName().toString().equalsIgnoreCase(CFG)
						|| o.getFileName().toString().equalsIgnoreCase(GIT))).forEach(o -> {
							if (names.remove(o.getFileName().toString())) {
								paths.add(o);
							}
						});
			}
		}
		return paths;
	}

	private static List<Path> dir(Path dir) throws IOException {
		List<Path> paths = new ArrayList<>();
		if (Files.exists(dir) && Files.isDirectory(dir)) {
			try (Stream<Path> stream = Files.list(dir)) {
				stream.filter(o -> StringUtils.endsWithAny(StringUtils.lowerCase(o.getFileName().toString()), JAR, ZIP))
						.forEach(paths::add);
			}
		}
		return paths;
	}

}
