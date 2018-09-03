package com.x.server.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.tools.DateTools;

public class Shadow {

	private static final String MANIFEST_FILENAME = "manifest.cfg";
	private static boolean tag = true;

	public static void main(String[] args) throws Exception {
		String base = getBasePath();
		loadJars(base);
		tag = true;
		File logFile = new File(Config.base(), "logs/" + DateTools.format(new Date(), "yyyy_MM_dd") + ".out.log");
		if (!logFile.exists()) {
			System.out.println("can not find log file,server not running.");
		} else {
			new Thread() {
				@Override
				public void run() {
					try (FileReader fr = new FileReader(logFile); BufferedReader br = new BufferedReader(fr)) {
						System.out.println("console start, type close to exit console.");
						br.skip(logFile.length());
						String line = null;
						while (tag) {
							if ((line = br.readLine()) != null) {
								System.out.println(line);
								continue;
							}
							try {
								Thread.sleep(2000L);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
								break;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
				String cmd = "";
				while (tag) {
					cmd = reader.readLine();
					if ((null == cmd) || (StringUtils.equalsIgnoreCase("close", StringUtils.trim(cmd)))) {
						tag = false;
						System.exit(0);
					}
					try (RandomAccessFile raf = new RandomAccessFile(getBasePath() + "/command.swap", "rw")) {
						FileChannel fc = raf.getChannel();
						MappedByteBuffer mbb = fc.map(MapMode.READ_WRITE, 0, 256);
						FileLock flock = null;
						flock = fc.lock();
						mbb.put(cmd.getBytes());
						flock.release();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
}
