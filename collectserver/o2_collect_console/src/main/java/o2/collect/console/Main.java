package o2.collect.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import o2.base.core.project.config.ApplicationServer;
import o2.base.core.project.config.Config;
import o2.base.core.project.config.DataServer;
import o2.base.core.project.config.WebServer;
import o2.collect.console.log.LogTools;
import o2.collect.console.server.Servers;

public class Main {

	private static final String MANIFEST_FILENAME = "manifest.cfg";

	public static void main(String[] args) throws Exception {
		String base = getBasePath();
		scanWar(base);
		loadJars(base);
		/* getVersion需要FileUtils在后面运行 */
		cleanTempDir(base);
		createTempClassesDirectory(base);
		SystemOutErrorSideCopyBuilder.start(base);
		LogTools.setSlf4jSimple();
		CommandFactory.printHelp();
		Matcher matcher = null;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			String cmd = "";
			for (;;) {
				try {
					cmd = reader.readLine();
				} catch (Exception e) {
					continue;
				}
				/** 在linux环境中当前端console窗口关闭后会导致可以立即read到一个null的input值 */
				if (null == cmd) {
					Thread.sleep(10000);
				}
				if (StringUtils.isBlank(cmd)) {
					continue;
				}
				matcher = CommandFactory.start_pattern.matcher(cmd);
				if (matcher.find()) {
					start();
					continue;
				}
				matcher = CommandFactory.stop_pattern.matcher(cmd);
				if (matcher.find()) {
					stop();
					continue;
				}
				matcher = CommandFactory.help_pattern.matcher(cmd);
				if (matcher.find()) {
					CommandFactory.printHelp();
					continue;
				}
				matcher = CommandFactory.exit_pattern.matcher(cmd);
				if (matcher.find()) {
					stop();
					break;
				}
				System.out.println("unknown command:" + cmd);
			}
		}
		SystemOutErrorSideCopyBuilder.stop();
	}

	private static void start() {
		try {
			DataServer dataServer = Config.dataServer();
			if (null != dataServer) {
				if (BooleanUtils.isTrue(dataServer.getEnable())) {
					startDataServer();
				}
			}
			ApplicationServer applicationServer = Config.applicationServer();
			if (null != applicationServer) {
				if (BooleanUtils.isTrue(applicationServer.getEnable())) {
					startApplicationServer();
				}
			}
			WebServer webServer = Config.webServer();
			if (null != webServer) {
				if (BooleanUtils.isTrue(webServer.getEnable())) {
					startWebServer();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void stop() {
		try {
			WebServer webServer = Config.webServer();
			if (null != webServer) {
				if (BooleanUtils.isTrue(webServer.getEnable())) {
					stopWebServer();
				}
			}
			ApplicationServer applicationServer = Config.applicationServer();
			if (null != applicationServer) {
				if (BooleanUtils.isTrue(applicationServer.getEnable())) {
					stopApplicationServer();
				}
			}
			DataServer dataServer = Config.dataServer();
			if (null != dataServer) {
				if (BooleanUtils.isTrue(dataServer.getEnable())) {
					stopDataServer();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void createTempClassesDirectory(String base) throws Exception {
		File tempDir = new File(base, "local/temp/classes");
		FileUtils.forceMkdir(tempDir);
		FileUtils.cleanDirectory(tempDir);
	}

	/**
	 * 检查store目录下的war文件是否全部在manifest.cfg中
	 * 
	 * @param base
	 *            o2server的根目录
	 */
	private static void scanWar(String base) throws Exception {
		File dir = new File(base, "store");
		File manifest = new File(dir, MANIFEST_FILENAME);
		if ((!manifest.exists()) || manifest.isDirectory()) {
			throw new Exception("can not find " + MANIFEST_FILENAME + " in store.");
		}
		List<String> manifestNames = readManifest(manifest);
		for (File o : dir.listFiles()) {
			if (o.isDirectory() && o.getName().equals("jars")) {
				continue;
			}
			if (o.getName().equals(MANIFEST_FILENAME)) {
				continue;
			}
			if (!manifestNames.contains(o.getName())) {
				o.delete();
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

	// private static String getVersion(String base) throws Exception {
	// File file = new File(base, "version.o2");
	// if (file.exists() && file.isFile()) {
	// return FileUtils.readFileToString(file);
	// }
	// throw new Exception("can not find version file.");
	// }

	private static void cleanTempDir(String base) throws Exception {
		File file = new File(base, "local/temp");
		FileUtils.forceMkdir(file);
		FileUtils.cleanDirectory(file);
	}

	// private static boolean executeUpdateAfterScript(String base, String
	// version) throws Exception {
	// File dir = new File(base, "local/updates/" + version);
	// if (dir.exists() && dir.isDirectory()) {
	// File file = new File(base, "local/updates/" + version + "/script/update"
	// + version + "after.jar");
	// if (file.exists() && file.isFile()) {
	// System.out.println("executing after update script.");
	// File tempFile = new File(base, "local/temp/" + file.getName());
	// FileUtils.copyFile(file, tempFile);
	// addJar(tempFile);
	// Class<?> clz = Class.forName("update" + version + "after.Main");
	// MethodUtils.invokeStaticMethod(clz, "main", new Object[] { new String[]
	// {} });
	// FileUtils.forceDelete(dir);
	// System.out.println("execute after update script sucess, should to restart
	// server.");
	// return false;
	// }
	// FileUtils.forceDelete(dir);
	// }
	// return true;
	// }

	// private static void addJar(File file) throws Exception {
	// URLClassLoader urlClassLoader = (URLClassLoader)
	// ClassLoader.getSystemClassLoader();
	// Class<?> urlClass = URLClassLoader.class;
	// Method method = urlClass.getDeclaredMethod("addURL", new Class[] {
	// URL.class });
	// method.setAccessible(true);
	// method.invoke(urlClassLoader, new Object[] { file.toURI().toURL() });
	// }

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

	private static void startDataServer() {
		try {
			if (Servers.dataServerIsRunning()) {
				System.out.println("data server is running.");
			} else {
				Servers.startDataServer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void stopDataServer() {
		try {
			if (!Servers.dataServerIsRunning()) {
				System.out.println("data server is not running.");
			} else {
				Servers.stopDataServer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void startApplicationServer() {
		try {
			if (Servers.applicationServerIsRunning()) {
				System.out.println("application server is running.");
			} else {
				Servers.startApplicationServer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void stopApplicationServer() {
		try {
			if (!Servers.applicationServerIsRunning()) {
				System.out.println("application server is not running.");
			} else {
				Servers.stopApplicationServer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void startWebServer() {
		try {
			if (Servers.webServerIsRunning()) {
				System.out.println("web server is running.");
			} else {
				Servers.startWebServer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void stopWebServer() {
		try {
			if (!Servers.webServerIsRunning()) {
				System.out.println("web server is not running.");
			} else {
				Servers.stopWebServer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}