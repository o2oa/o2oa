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
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.x.base.core.project.server.ApplicationServer;
import com.x.base.core.project.server.Config;
import com.x.base.core.project.server.DataServer;
import com.x.base.core.project.server.StorageServer;
import com.x.base.core.project.server.WebServer;
import com.x.server.console.action.ActionConfig;
import com.x.server.console.action.ActionSetPassword;
import com.x.server.console.action.ActionUpdate;
import com.x.server.console.action.ActionVersion;
import com.x.server.console.log.LogTools;
import com.x.server.console.server.Servers;
import com.x.server.console.tools.dumpdata.DumpData;
import com.x.server.console.tools.dumpdata.RestoreData;
import com.x.server.console.tools.dumpdata.dumpstorage.DumpStorage;
import com.x.server.console.tools.dumpdata.dumpstorage.RestoreStorage;

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
		Boolean go = executeUpdateAfterScript(base, Config.version());
		startSwapCommand();
		if (go) {
			CommandFactory.printHelp(base, Config.version());
			Matcher matcher = null;
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
				String cmd = "";
				for (;;) {
					try {
						cmd = reader.readLine();
					} catch (Exception e) {
						continue;
					}
					if (StringUtils.isBlank(cmd)) {
						continue;
					}
					matcher = CommandFactory.start_pattern.matcher(cmd);
					if (matcher.find()) {
						switch (matcher.group(1)) {
						case "application":
							startApplicationServer();
							break;
						case "center":
							startCenterServer();
							break;
						case "web":
							startWebServer();
							break;
						case "storage":
							startStorageServer();
							break;
						case "data":
							startDataServer();
							break;
						default:
							startAll();
							break;
						}
						continue;
					}
					matcher = CommandFactory.stop_pattern.matcher(cmd);
					if (matcher.find()) {
						switch (matcher.group(1)) {
						case "application":
							stopApplicationServer();
							break;
						case "center":
							stopCenterServer();
							break;
						case "web":
							stopWebServer();
							break;
						case "storage":
							stopStorageServer();
							break;
						case "data":
							stopDataServer();
							break;
						default:
							stopAll();
							break;
						}
						continue;
					}
					matcher = CommandFactory.dump_pattern.matcher(cmd);
					if (matcher.find()) {
						switch (matcher.group(1)) {
						case "data":
							dumpData();
							break;
						case "storage":
							dumpStorage();
							break;
						default:
							break;
						}
						continue;
					}
					matcher = CommandFactory.restore_pattern.matcher(cmd);
					if (matcher.find()) {
						switch (matcher.group(1)) {
						case "data":
							resotreData(base, matcher.group(2));
							break;
						case "storage":
							resotreStorage(base, matcher.group(2));
							break;
						default:
							break;
						}
						continue;
					}
					matcher = CommandFactory.help_pattern.matcher(cmd);
					if (matcher.find()) {
						CommandFactory.printHelp(base, Config.version());
						continue;
					}

					matcher = CommandFactory.version_pattern.matcher(cmd);
					if (matcher.find()) {
						version(base);
						continue;
					}

					matcher = CommandFactory.update_pattern.matcher(cmd);
					if (matcher.find()) {
						stopAll();
						if (update(base)) {
							break;
						} else {
							continue;
						}
					}

					matcher = CommandFactory.config_pattern.matcher(cmd);
					if (matcher.find()) {
						stopAll();
						if (config()) {
							break;
						} else {
							continue;
						}
					}

					matcher = CommandFactory.setPassword_pattern.matcher(cmd);
					if (matcher.find()) {
						setPassword(matcher.group(1), matcher.group(2));
						if (config()) {
							break;
						} else {
							continue;
						}
					}

					matcher = CommandFactory.exit_pattern.matcher(cmd);
					if (matcher.find()) {
						exit();
					}

					System.out.println("unknown command:" + cmd);
				}
			}
		}
		SystemOutErrorSideCopyBuilder.stop();
	}

	private static boolean update(String base) {
		try {
			return new ActionUpdate().execute(base);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private static void version(String base) {
		try {
			new ActionVersion().execute(base);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean config() {
		try {
			return new ActionConfig().execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
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

	private static void startStorageServer() {
		try {
			if (Servers.storageServerIsRunning()) {
				System.out.println("storage server is running.");
			} else {
				Servers.startStorageServer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void stopStorageServer() {
		try {
			if (!Servers.storageServerIsRunning()) {
				System.out.println("storage server is not running.");
			} else {
				Servers.stopStorageServer();
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

	private static void startCenterServer() {
		try {
			if (Servers.centerServerIsRunning()) {
				System.out.println("center server is running.");
			} else {
				Servers.startCenterServer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void stopCenterServer() {
		try {
			if (!Servers.centerServerIsRunning()) {
				System.out.println("center server is not running.");
			} else {
				Servers.stopCenterServer();
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

	private static void startAll() {
		try {
			DataServer dataServer = Config.currentNode().getData();
			if (null != dataServer) {
				if (BooleanUtils.isTrue(dataServer.getEnable())) {
					startDataServer();
				}
			}
			StorageServer storageServer = Config.currentNode().getStorage();
			if (null != storageServer) {
				if (BooleanUtils.isTrue(storageServer.getEnable())) {
					startStorageServer();
				}
			}
			if (Config.currentNode().getIsPrimaryCenter()) {
				startCenterServer();
			}
			ApplicationServer applicationServer = Config.currentNode().getApplication();
			if (null != applicationServer) {
				if (BooleanUtils.isTrue(applicationServer.getEnable())) {
					startApplicationServer();
				}
			}
			WebServer webServer = Config.currentNode().getWeb();
			if (null != webServer) {
				if (BooleanUtils.isTrue(webServer.getEnable())) {
					startWebServer();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void exit() {
		stopAll();
		System.exit(0);
	}

	private static void stopAll() {
		try {
			WebServer webServer = Config.currentNode().getWeb();
			if (null != webServer) {
				if (BooleanUtils.isTrue(webServer.getEnable())) {
					stopWebServer();
				}
			}
			ApplicationServer applicationServer = Config.currentNode().getApplication();
			if (null != applicationServer) {
				if (BooleanUtils.isTrue(applicationServer.getEnable())) {
					stopApplicationServer();
				}
			}
			if (Config.currentNode().getIsPrimaryCenter()) {
				stopCenterServer();
			}
			StorageServer storageServer = Config.currentNode().getStorage();
			if (null != storageServer) {
				if (BooleanUtils.isTrue(storageServer.getEnable())) {
					stopStorageServer();
				}
			}
			DataServer dataServer = Config.currentNode().getData();
			if (null != dataServer) {
				if (BooleanUtils.isTrue(dataServer.getEnable())) {
					stopDataServer();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void dumpData() {
		try {
			(new DumpData()).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void dumpStorage() {
		try {
			(new DumpStorage()).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void resotreData(String base, String dateString) throws Exception {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			Date date = format.parse(dateString);
			File file = new File(base, "local/dump/dumpData_" + format.format(date));
			if (file.exists() && file.isDirectory()) {
				RestoreData restoreData = new RestoreData();
				restoreData.execute(date);
			} else {
				System.out.println("directory " + file.getAbsolutePath() + " not existed.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void resotreStorage(String base, String dateString) throws Exception {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			Date date = format.parse(dateString);
			File file = new File(base, "local/dump/dumpStorage_" + format.format(date));
			if (file.exists() && file.isDirectory()) {
				RestoreStorage restoreStorage = new RestoreStorage();
				restoreStorage.execute(date);
			} else {
				System.out.println("directory " + file.getAbsolutePath() + " not existed.");
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

	private static boolean executeUpdateAfterScript(String base, String version) throws Exception {
		File dir = new File(base, "local/updates/" + version);
		if (dir.exists() && dir.isDirectory()) {
			File file = new File(base, "local/updates/" + version + "/script/update" + version + "after.jar");
			if (file.exists() && file.isFile()) {
				System.out.println("executing after update script.");
				File tempFile = new File(base, "local/temp/" + file.getName());
				FileUtils.copyFile(file, tempFile);
				addJar(tempFile);
				Class<?> clz = Class.forName("update" + version + "after.Main");
				MethodUtils.invokeStaticMethod(clz, "main", new Object[] { new String[] {} });
				FileUtils.forceDelete(dir);
				System.out.println("execute after update script sucess, should to restart server.");
				return false;
			}
			FileUtils.forceDelete(dir);
		}
		return true;
	}

	private static void addJar(File file) throws Exception {
		URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<?> urlClass = URLClassLoader.class;
		Method method = urlClass.getDeclaredMethod("addURL", new Class[] { URL.class });
		method.setAccessible(true);
		method.invoke(urlClassLoader, new Object[] { file.toURI().toURL() });
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

	private static boolean setPassword(String oldPassword, String newPassword) throws Exception {
		try {
			return new ActionSetPassword().execute(oldPassword, newPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static void startSwapCommand() {
		new Thread() {
			public void run() {
				try (RandomAccessFile raf = new RandomAccessFile(Config.base() + "/command.swap", "rw")) {
					FileChannel fc = raf.getChannel();
					MappedByteBuffer mbb = fc.map(MapMode.READ_WRITE, 0, 64);
					byte[] bs = new byte[64];
					Arrays.fill(bs, (byte) 0);
					mbb.put(bs);
					FileLock flock = null;
					byte b;
					while (true) {
						flock = fc.lock();
						b = mbb.get(0);
						mbb.put(0, (byte) 0);
						flock.release();
						if (b == 1) {
							exit();
						} else {
							Thread.sleep(3000);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}