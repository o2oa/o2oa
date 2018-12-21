package com.x.server.console.swapcommand;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.config.Config;
import com.x.server.console.Main;

public class Exit {

	private static final String MANIFEST_FILENAME = "manifest.cfg";

	public static void main(String... args) throws Exception {
		String base = getBasePath();
		loadJars(base);
		try (RandomAccessFile raf = new RandomAccessFile(Config.base() + "/command.swap", "rw")) {
			FileChannel fc = raf.getChannel();
			MappedByteBuffer mbb = fc.map(MapMode.READ_WRITE, 0, 256);
			FileLock flock = null;
			flock = fc.lock();
			mbb.put("exit".getBytes());
			flock.release();
		}
	}

	private static List<String> readManifest(File file) throws Exception {
		List<String> list = new ArrayList<>();
		try (FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader)) {
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				list.add(line);
			}
		}
		return list;
	}

	private static void loadJars(String base) throws Exception {
		URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<?> urlClass = URLClassLoader.class;
		Method method = urlClass.getDeclaredMethod("addURL", new Class[] { URL.class });
		method.setAccessible(true);
		/* loading ext */
		File extDir = new File(base, "commons/ext");
		File extDirManifest = new File(extDir, MANIFEST_FILENAME);
		if (!extDirManifest.exists()) {
			throw new Exception("can not find " + MANIFEST_FILENAME + " in commons/ext.");
		}
		List<String> extDirManifestNames = readManifest(extDirManifest);
		if (extDirManifestNames.isEmpty()) {
			throw new Exception("commons/ext manifest is empty.");
		}
		for (File file : extDir.listFiles()) {
			if (!file.getName().equals(MANIFEST_FILENAME)) {
				if (!extDirManifestNames.contains(file.getName())) {
					file.delete();
				} else {
					method.invoke(urlClassLoader, new Object[] { file.toURI().toURL() });
				}
			}
		}
		/* loading jars */
		File jarsDir = new File(base, "store/jars");
		File jarsDirManifest = new File(jarsDir, MANIFEST_FILENAME);
		if (!jarsDirManifest.exists()) {
			throw new Exception("can not find " + MANIFEST_FILENAME + " in store/jars.");
		}
		List<String> jarsDirManifestNames = readManifest(jarsDirManifest);
		for (File file : jarsDir.listFiles()) {
			if (!file.getName().equals(MANIFEST_FILENAME)) {
				if (!jarsDirManifestNames.contains(file.getName())) {
					file.delete();
				} else {
					method.invoke(urlClassLoader, new Object[] { file.toURI().toURL() });
				}
			}
		}
		File tempDir = new File(base, "local/temp/classes");
		method.invoke(urlClassLoader, new Object[] { tempDir.toURI().toURL() });
	}

	private static String getBasePath() throws Exception {
		String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		File file = new File(path);
		if (!file.isDirectory()) {
			file = file.getParentFile();
		}
		while (null != file) {
			File versionFile = new File(file, "version.o2");
			if (versionFile.exists()) {
				return file.getAbsolutePath();
			}
			file = file.getParentFile();
		}
		throw new Exception("can not define o2server base directory.");
	}

}
