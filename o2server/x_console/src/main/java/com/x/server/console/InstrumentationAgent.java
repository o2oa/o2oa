package com.x.server.console;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class InstrumentationAgent {

	private static Instrumentation INST;

	private static final String CFG = "manifest.cfg";
	private static final String GIT = ".gitignore";

	public static void agentmain(String args, Instrumentation inst) {
		INST = inst;
		try {
			Path base = getBasePath();
			if (Files.exists(base.resolve("custom/jars"))) {
				load(base, "custom/jars");
			}
			if (Files.exists(base.resolve("dynamic/jars"))) {
				load(base, "dynamic/jars");
			}
			load(base, "store/jars");
			load(base, "commons/ext");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void premain(String args, Instrumentation inst) {
		INST = inst;
		try {
			Path base = getBasePath();
			if (Files.exists(base.resolve("custom/jars"))) {
				load(base, "custom/jars");
			}
			if (Files.exists(base.resolve("dynamic/jars"))) {
				load(base, "dynamic/jars");
			}
			load(base, "store/jars");
			load(base, "commons/ext");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void load(Path base, String sub) throws Exception {
		Path dir = base.resolve(sub);
		Path cfg = dir.resolve(CFG);
		if (Files.exists(dir) && Files.isDirectory(dir) && Files.exists(cfg) && Files.isRegularFile(cfg)) {
			List<String> names = Files.readAllLines(cfg);
			if (names.isEmpty()) {
				throw new Exception(String.format("%s manifest is empty.", sub));
			}
			try (Stream<Path> stream = Files.list(dir)) {
				stream.filter(o -> !(o.getFileName().toString().equalsIgnoreCase(CFG)
						|| o.getFileName().toString().equalsIgnoreCase(GIT))).forEach(o -> {
							try {
								if (names.remove(o.getFileName().toString())) {
									// addURLToClassPath(o);
									System.out.println("load jar:" + o.toString());
									INST.appendToSystemClassLoaderSearch(new JarFile(o.toString()));
								} else {
									Files.delete(o);
									System.out.printf("delete unnecessary file from %s: %s.", sub,
											o.getFileName().toString());
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						});
			}
			for (String name : names) {
				System.out.printf("can not load jar from %s: %s", sub, name);
			}
		} else {
			throw new Exception(String.format("invalid directory: %s", sub));
		}
	}

	private static Path getBasePath() throws Exception {
		Path path = Paths.get(InstrumentationAgent.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		Path version = path.resolveSibling("version.o2");
		if (Files.exists(version) && Files.isRegularFile(version)) {
			return version.getParent();
		}
		throw new Exception("can not define o2server base directory.");
	}

	private static void addURLToClassPath(Path jarPath) {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		try {
			if (classLoader instanceof URLClassLoader) {
				Method method = classLoader.getClass().getDeclaredMethod("addURL", URL.class);
				method.setAccessible(true);
				method.invoke(classLoader, jarPath.toUri().toURL());
			} else {
				Method method = classLoader.getClass().getDeclaredMethod("appendToClassPathForInstrumentation",
						String.class);
				method.setAccessible(true);
				method.invoke(classLoader, jarPath.toAbsolutePath().toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
