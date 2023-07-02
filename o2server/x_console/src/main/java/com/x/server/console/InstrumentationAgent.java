package com.x.server.console;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class InstrumentationAgent {

	private InstrumentationAgent() {
		// nothing
	}

	private static Instrumentation INST;

	private static final String CFG = "manifest.cfg";
	private static final String GIT = ".gitignore";
	private static final String CUSTOM_JARS = "custom/jars";
	// private static final String DYNAMIC_JARS = "dynamic/jars";
	private static final String STORE_JARS = "store/jars";
	private static final String STORE_JARS_BASE_CORE_PROJECT = STORE_JARS + "/" + "x_base_core_project.jar";
	private static final String COMMONS_EXT = "commons/ext";

	public static String JAVAVERSION = "java8";

	public static String OS = "windows";

	public static final String JAVAVERSION_JAVA8 = "java8";
	public static final String JAVAVERSION_JAVA11 = "java11";

	public static final String OS_WINDOWS = "windows";
	public static final String OS_LINUX = "linux";
	public static final String OS_MACOS = "macos";
	public static final String OS_RASPI = "raspi";
	public static final String OS_ARM = "arm";
	public static final String OS_MIPS = "mips";

	public static final String INSTRUMENTATIONTYPESHADOW = "shadow";

	public static void premain(String args, Instrumentation inst) {
		String version = System.getProperty("java.vm.specification.version");
		if (version.startsWith("1.8")) {
			JAVAVERSION = JAVAVERSION_JAVA8;
		} else {
			JAVAVERSION = JAVAVERSION_JAVA11;
		}
		INST = inst;
		try {
			Path base = getBasePath();
			if (Files.exists(base.resolve(CUSTOM_JARS))) {
				load(base, CUSTOM_JARS);
			}
//			if (Files.exists(base.resolve(DYNAMIC_JARS))) {
//				load(base, DYNAMIC_JARS);
//			}
			setLog4j2(base, args);
			checkStoreJarWithCfg(base);
			loadBaseCoreProject(base);
			// loadWithCfg(base, STORE_JARS);
			loadWithCfg(base, ext());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String ext() {
		return JAVAVERSION.equals(JAVAVERSION_JAVA8) ? COMMONS_EXT : COMMONS_EXT + "_" + JAVAVERSION;
	}

	private static void checkStoreJarWithCfg(Path base) throws IOException {
		Path dir = base.resolve(STORE_JARS);
		Path cfg = dir.resolve(CFG);
		if (Files.exists(dir) && Files.isDirectory(dir) && Files.exists(cfg) && Files.isRegularFile(cfg)) {
			List<String> names = Files.readAllLines(cfg);
			if (names.isEmpty()) {
				throw new IOException(String.format("%s manifest is empty.", STORE_JARS));
			}
			try (Stream<Path> stream = Files.list(dir)) {
				stream.filter(o -> !(o.getFileName().toString().equalsIgnoreCase(CFG)
						|| o.getFileName().toString().equalsIgnoreCase(GIT))).forEach(o -> {
							try {
								if (!names.remove(o.getFileName().toString())) {
									Files.delete(o);
									System.out.println(String.format("delete unnecessary file from %s: %s.", STORE_JARS,
											o.getFileName().toString()));
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						});
			}
			for (String name : names) {
				System.out.println(String.format("can not find jar from %s: %s", STORE_JARS, name));
			}
		} else {
			throw new IOException(String.format("invalid directory: %s", STORE_JARS));
		}
	}

	private static void loadBaseCoreProject(Path base) throws IOException {
		INST.appendToSystemClassLoaderSearch(new JarFile(base.resolve(STORE_JARS_BASE_CORE_PROJECT).toString()));
	}

	private static void loadWithCfg(Path base, String sub) throws IOException {
		Path dir = base.resolve(sub);
		Path cfg = dir.resolve(CFG);
		if (Files.exists(dir) && Files.isDirectory(dir) && Files.exists(cfg) && Files.isRegularFile(cfg)) {
			List<String> names = Files.readAllLines(cfg);
			if (names.isEmpty()) {
				throw new IOException(String.format("%s manifest is empty.", sub));
			}
			try (Stream<Path> stream = Files.list(dir)) {
				stream.filter(o -> !(o.getFileName().toString().equalsIgnoreCase(CFG)
						|| o.getFileName().toString().equalsIgnoreCase(GIT))).forEach(o -> {
							try {
								if (names.remove(o.getFileName().toString())) {
									INST.appendToSystemClassLoaderSearch(new JarFile(o.toString()));
								} else {
									Files.delete(o);
									System.out.println(String.format("delete unnecessary file from %s: %s.", sub,
											o.getFileName().toString()));
								}

							} catch (IOException e) {
								e.printStackTrace();
							}
						});
			}
			for (String name : names) {
				System.out.println(String.format("can not load jar from %s: %s", sub, name));
			}
		} else {
			throw new IOException(String.format("invalid directory: %s", sub));
		}
	}

	private static void load(Path base, String sub) throws IOException {
		Path dir = base.resolve(sub);
		if (Files.exists(dir) && Files.isDirectory(dir)) {
			try (Stream<Path> stream = Files.list(dir)) {
				stream.filter(
						o -> o.toString().toLowerCase().endsWith(".zip") || o.toString().toLowerCase().endsWith(".jar"))
						.forEach(o -> {
							try {
								INST.appendToSystemClassLoaderSearch(new JarFile(o.toString()));
							} catch (IOException e) {
								System.err.println("file: " + o.toString() + " load error!");
							}
						});
			}
		} else {
			throw new IOException(String.format("invalid directory: %s", sub));
		}
	}

	private static Path getBasePath() throws IOException, URISyntaxException {
		Path path = Paths.get(InstrumentationAgent.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		Path version = path.resolveSibling("version.o2");
		if (Files.exists(version) && Files.isRegularFile(version)) {
			return version.getParent();
		}
		throw new IOException("can not define o2server base directory.");
	}

	private static void setLog4j2(Path base, String args) {
		if (!Objects.equals(args, INSTRUMENTATIONTYPESHADOW)) {
			System.setProperty("log4j.configurationFile", base.resolve("commons").resolve("log4j2.xml").toString());
		}
	}

}
