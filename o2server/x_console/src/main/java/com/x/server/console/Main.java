package com.x.server.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
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
import org.eclipse.jetty.deploy.App;
import org.eclipse.jetty.deploy.DeploymentManager;
import org.quartz.Scheduler;

import com.x.base.core.project.config.ApplicationServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DataServer;
import com.x.base.core.project.config.StorageServer;
import com.x.base.core.project.config.WebServer;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.server.console.action.ActionCompactData;
import com.x.server.console.action.ActionConfig;
import com.x.server.console.action.ActionCreateEncryptKey;
import com.x.server.console.action.ActionDumpData;
import com.x.server.console.action.ActionDumpStorage;
import com.x.server.console.action.ActionEraseContentBbs;
import com.x.server.console.action.ActionEraseContentCms;
import com.x.server.console.action.ActionEraseContentLog;
import com.x.server.console.action.ActionEraseContentProcessPlatform;
import com.x.server.console.action.ActionRestoreData;
import com.x.server.console.action.ActionRestoreStorage;
import com.x.server.console.action.ActionSetPassword;
import com.x.server.console.action.ActionShowCpu;
import com.x.server.console.action.ActionShowDataSource;
import com.x.server.console.action.ActionShowMemory;
import com.x.server.console.action.ActionShowOs;
import com.x.server.console.action.ActionShowThread;
import com.x.server.console.action.ActionUpdateFile;
import com.x.server.console.action.ActionVersion;
import com.x.server.console.log.LogTools;
import com.x.server.console.server.Servers;

public class Main {

	private static final String MANIFEST_FILENAME = "manifest.cfg";
	private static final String GITIGNORE_FILENAME = ".gitignore";

	public static void main(String[] args) throws Exception {
		String base = getBasePath();
		scanWar(base);
		loadJars(base);
		/* getVersion需要FileUtils在后面运行 */
		cleanTempDir(base);
		createTempClassesDirectory(base);
		LogTools.setSlf4jSimple();
		SystemOutErrorSideCopyBuilder.start();
		ResourceFactory.bind();
		CommandFactory.printStartHelp();
		/* 以下可以使用Config */
		if (null == Config.currentNode()) {
			throw new Exception("无法找到当前节点,请检查config/node_{name}.json与local/node.cfg文件内容中的名称是否一致.");
		}
		try (PipedInputStream pipedInput = new PipedInputStream();
				PipedOutputStream pipedOutput = new PipedOutputStream(pipedInput)) {
			new Thread() {
				/* 文件中的命令输出到解析器 */
				public void run() {
					try (RandomAccessFile raf = new RandomAccessFile(Config.base() + "/command.swap", "rw")) {
						FileChannel fc = raf.getChannel();
						MappedByteBuffer mbb = fc.map(MapMode.READ_WRITE, 0, 256);
						byte[] fillBytes = new byte[256];
						byte[] readBytes = new byte[256];
						Arrays.fill(fillBytes, (byte) 0);
						mbb.put(fillBytes);
						FileLock flock = null;
						String cmd = "";
						while (true) {
							flock = fc.lock();
							mbb.position(0);
							mbb.get(readBytes, 0, 256);
							mbb.position(0);
							mbb.put(fillBytes);
							flock.release();
							if (!Arrays.equals(readBytes, fillBytes)) {
								cmd = StringUtils.trim(new String(readBytes, DefaultCharset.charset));
								System.out.println("read command:" + cmd);
								pipedOutput.write((cmd + StringUtils.LF).getBytes(DefaultCharset.name));
							}
							Thread.sleep(4000);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
			new Thread() {
				/* 将屏幕命令输出到解析器 */
				public void run() {
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
						String cmd = "";
						while (null != cmd) {
							cmd = reader.readLine();
							/** 在linux环境中当前端console窗口关闭后会导致可以立即read到一个null的input值 */
							if (null != cmd) {
								cmd = cmd + StringUtils.LF;
								pipedOutput.write(cmd.getBytes(DefaultCharset.name));
								pipedOutput.flush();
							}
							Thread.sleep(1000);
						}
						System.out.println("console input closed!");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
			/* 启动NodeAgent */
			if (BooleanUtils.isTrue(Config.currentNode().nodeAgentEnable())) {
				NodeAgent nodeAgent = new NodeAgent();
				nodeAgent.start();
			}

			SchedulerBuilder schedulerBuilder = new SchedulerBuilder();
			Scheduler scheduler = schedulerBuilder.start();

			if (Config.currentNode().autoStart()) {
				startAll();
			}

			Matcher matcher = null;
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(pipedInput))) {
				String cmd = "";
				while (true) {
					try {
						cmd = reader.readLine();
					} catch (Exception e) {
						continue;
					}
					if (StringUtils.isBlank(cmd)) {
						continue;
					}

					matcher = CommandFactory.test_pattern.matcher(cmd);
					if (matcher.find()) {
						test();
						continue;
					}

					matcher = CommandFactory.show_os_pattern.matcher(cmd);
					if (matcher.find()) {
						showOs(matcher.group(1), matcher.group(2));
						continue;
					}

					matcher = CommandFactory.show_cpu_pattern.matcher(cmd);
					if (matcher.find()) {
						showCpu(matcher.group(1), matcher.group(2));
						continue;
					}

					matcher = CommandFactory.show_memory_pattern.matcher(cmd);
					if (matcher.find()) {
						showMemory(matcher.group(1), matcher.group(2));
						continue;
					}

					matcher = CommandFactory.show_thread_pattern.matcher(cmd);
					if (matcher.find()) {
						showThread(matcher.group(1), matcher.group(2));
						continue;
					}

					matcher = CommandFactory.show_dataSource_pattern.matcher(cmd);
					if (matcher.find()) {
						showDataSource(matcher.group(1), matcher.group(2));
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
					matcher = CommandFactory.dump_path_pattern.matcher(cmd);
					if (matcher.find()) {
						switch (matcher.group(1)) {
						case "data":
							dumpData(matcher.group(2), matcher.group(3));
							break;
						case "storage":
							dumpStorage(matcher.group(2), matcher.group(3));
							break;
						default:
							break;
						}
						continue;
					}
					matcher = CommandFactory.dump_pattern.matcher(cmd);
					if (matcher.find()) {
						switch (matcher.group(1)) {
						case "data":
							dumpData("", matcher.group(2));
							break;
						case "storage":
							dumpStorage("", matcher.group(2));
							break;
						default:
							break;
						}
						continue;
					}
					matcher = CommandFactory.restore_path_pattern.matcher(cmd);
					if (matcher.find()) {
						switch (matcher.group(1)) {
						case "data":
							resotreDataPath(matcher.group(2), matcher.group(3));
							break;
						case "storage":
							resotreStoragePath(matcher.group(2), matcher.group(3));
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
							resotreData(matcher.group(2), matcher.group(3));
							break;
						case "storage":
							resotreStorage(matcher.group(2), matcher.group(3));
							break;
						default:
							break;
						}
						continue;
					}
					matcher = CommandFactory.help_pattern.matcher(cmd);
					if (matcher.find()) {
						CommandFactory.printHelp();
						continue;
					}

					matcher = CommandFactory.version_pattern.matcher(cmd);
					if (matcher.find()) {
						version();
						continue;
					}

					matcher = CommandFactory.updateFile_pattern.matcher(cmd);
					if (matcher.find()) {
						if (updateFile(matcher.group(1), matcher.group(2), matcher.group(3))) {
							stopAll();
							System.exit(0);
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

					matcher = CommandFactory.erase_content_pattern.matcher(cmd);
					if (matcher.find()) {
						switch (matcher.group(1)) {
						case "pp":
							eraseContentProcessPlatform(matcher.group(2));
							break;
						case "cms":
							eraseContentCms(matcher.group(2));
							break;
						case "log":
							eraseContentLog(matcher.group(2));
							break;
						case "bbs":
							eraseContentBbs(matcher.group(2));
							break;

						default:
							break;
						}
						continue;
					}

					matcher = CommandFactory.compact_data_pattern.matcher(cmd);
					if (matcher.find()) {
						compactData(matcher.group(1));
						continue;
					}

					matcher = CommandFactory.create_encrypt_key_pattern.matcher(cmd);
					if (matcher.find()) {
						createEncryptKey(matcher.group(1));
						continue;
					}

					matcher = CommandFactory.exit_pattern.matcher(cmd);
					if (matcher.find()) {
						exit();
					}

					System.out.println("unknown command:" + cmd);
				}
			}
			/* 关闭定时器 */
			scheduler.shutdown();
		}
		SystemOutErrorSideCopyBuilder.stop();
	}

	private static boolean test() {
		try {
			DeploymentManager deployer = Servers.applicationServer.getBean(DeploymentManager.class);
			for (App app : deployer.getApps()) {
				System.out.println(app.getContextPath());
				if (StringUtils.equals("/x_query_assemble_designer", app.getContextPath())) {
					app.getContextHandler().stop();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private static boolean showOs(String interval, String repeat) {
		try {
			return new ActionShowOs().execute(Integer.parseInt(interval, 10), Integer.parseInt(repeat, 10));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private static boolean showCpu(String interval, String repeat) {
		try {
			return new ActionShowCpu().execute(Integer.parseInt(interval, 10), Integer.parseInt(repeat, 10));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private static boolean showMemory(String interval, String repeat) {
		try {
			return new ActionShowMemory().execute(Integer.parseInt(interval, 10), Integer.parseInt(repeat, 10));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private static boolean showThread(String interval, String repeat) {
		try {
			return new ActionShowThread().execute(Integer.parseInt(interval, 10), Integer.parseInt(repeat, 10));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private static boolean showDataSource(String interval, String repeat) {
		try {
			return new ActionShowDataSource().execute(Integer.parseInt(interval, 10), Integer.parseInt(repeat, 10));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private static boolean createEncryptKey(String password) {
		try {
			return new ActionCreateEncryptKey().execute(password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private static boolean updateFile(String path, String backup, String password) {
		try {
			return new ActionUpdateFile().execute(path, BooleanUtils.toBoolean(backup), password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private static void version() {
		try {
			new ActionVersion().execute();
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

	private static boolean dumpData(String path, String password) {
		try {
			return (new ActionDumpData()).execute(path, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private static boolean dumpStorage(String path, String password) {
		try {
			return (new ActionDumpStorage()).execute(path, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private static void resotreData(String dateString, String password) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			Date date = format.parse(dateString);
			File file = new File(Config.base(), "local/dump/dumpData_" + format.format(date));
			if (file.exists() && file.isDirectory()) {
				(new ActionRestoreData()).execute(date, password);
			} else {
				System.out.println("directory " + file.getAbsolutePath() + " not existed.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void resotreDataPath(String path, String password) {
		try {
			(new ActionRestoreData()).execute(path, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void resotreStorage(String dateString, String password) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			Date date = format.parse(dateString);
			File file = new File(Config.base(), "local/dump/dumpStorage_" + format.format(date));
			if (file.exists() && file.isDirectory()) {
				ActionRestoreStorage restoreStorage = new ActionRestoreStorage();
				restoreStorage.execute(date, password);
			} else {
				System.out.println("directory " + file.getAbsolutePath() + " not existed.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void resotreStoragePath(String path, String password) {
		try {
			new ActionRestoreStorage().execute(path, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void createTempClassesDirectory(String base) throws Exception {
		File local_temp_classes_dir = new File(base, "local/temp/classes");
		FileUtils.forceMkdir(local_temp_classes_dir);
		FileUtils.cleanDirectory(local_temp_classes_dir);
	}

	/**
	 * 检查store目录下的war文件是否全部在manifest.cfg中
	 * 
	 * @param base o2server的根目录
	 */
	private static void scanWar(String base) throws Exception {
		File dir = new File(base, "store");
		File manifest = new File(dir, MANIFEST_FILENAME);
		if ((!manifest.exists()) || manifest.isDirectory()) {
			System.out.println("启动过程忽略扫描 store 目录.");
			return;
		}
		List<String> manifestNames = readManifest(manifest);
		for (File o : dir.listFiles()) {
			if (o.isDirectory() && o.getName().equals("jars")) {
				continue;
			}
			if (o.getName().equals(MANIFEST_FILENAME)) {
				continue;
			}
			if (o.getName().equals(GITIGNORE_FILENAME)) {
				continue;
			}
			if (!manifestNames.contains(o.getName())) {
				System.out.println("扫描 store 过程中删除无效的文件:" + o.getName());
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
		File commons_ext_dir = new File(base, "commons/ext");
		File commons_ext_manifest_file = new File(commons_ext_dir, MANIFEST_FILENAME);
		if (commons_ext_manifest_file.exists() && commons_ext_manifest_file.isFile()) {
			List<String> commons_ext_manifest_names = readManifest(commons_ext_manifest_file);
			if (commons_ext_manifest_names.isEmpty()) {
				throw new Exception("commons/ext manifest is empty.");
			}
			for (File file : commons_ext_dir.listFiles()) {
				if ((!file.getName().equals(MANIFEST_FILENAME)) && (!file.getName().equals(GITIGNORE_FILENAME))) {
					if (!commons_ext_manifest_names.remove(file.getName())) {
						System.out.println("载入 commons/ext 过程中删除无效的文件:" + file.getName());
						file.delete();
					}
				}
			}
			for (String str : commons_ext_manifest_names) {
				System.out.println("载入 commons/ext 过程中无法找到文件:" + str);
			}
		} else {
			System.out.println("启动过程忽略扫描 commons/ext 目录.");
		}
		for (File file : commons_ext_dir.listFiles()) {
			if ((!file.getName().equals(MANIFEST_FILENAME)) && (!file.getName().equals(GITIGNORE_FILENAME))) {
				method.invoke(urlClassLoader, new Object[] { file.toURI().toURL() });
			}
		}
		/* loading jars */
		File store_jars_dir = new File(base, "store/jars");
		File store_jars_manifest_file = new File(store_jars_dir, MANIFEST_FILENAME);
		if (store_jars_manifest_file.exists() && store_jars_manifest_file.isFile()) {
			List<String> store_jars_manifest_names = readManifest(store_jars_manifest_file);
			for (File file : store_jars_dir.listFiles()) {
				if ((!file.getName().equals(MANIFEST_FILENAME)) && (!file.getName().equals(GITIGNORE_FILENAME))) {
					if (!store_jars_manifest_names.remove(file.getName())) {
						System.out.println("载入 store/jars 过程中删除无效的文件:" + file.getName());
						file.delete();
					}
				}
			}
			for (String str : store_jars_manifest_names) {
				System.out.println("载入 store/jars 过程中无法找到文件:" + str);
			}
		} else {
			System.out.println("启动过程忽略扫描 store/jars 目录.");
		}
		for (File file : store_jars_dir.listFiles()) {
			if ((!file.getName().equals(MANIFEST_FILENAME)) && (!file.getName().equals(GITIGNORE_FILENAME))) {
				method.invoke(urlClassLoader, new Object[] { file.toURI().toURL() });
			}
		}
		/* load custom jar */
		File custom_jars_dir = new File(base, "custom/jars");
		if (custom_jars_dir.exists() && custom_jars_dir.isDirectory()) {
			for (File file : custom_jars_dir.listFiles()) {
				method.invoke(urlClassLoader, new Object[] { file.toURI().toURL() });
			}
		}
		File dynamic_jars_dir = new File(base, "dynamic/jars");
		if (dynamic_jars_dir.exists() && dynamic_jars_dir.isDirectory()) {
			for (File file : dynamic_jars_dir.listFiles()) {
				method.invoke(urlClassLoader, new Object[] { file.toURI().toURL() });
			}
		}
		/* load temp class */
		File local_temp_classes_dir = new File(base, "local/temp/classes");
		method.invoke(urlClassLoader, new Object[] { local_temp_classes_dir.toURI().toURL() });
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

	private static void cleanTempDir(String base) throws Exception {
		File local_temp_dir = new File(base, "local/temp");
		FileUtils.forceMkdir(local_temp_dir);
		FileUtils.cleanDirectory(local_temp_dir);
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

	private static boolean compactData(String password) throws Exception {
		try {
			return new ActionCompactData().execute(password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static boolean eraseContentProcessPlatform(String password) throws Exception {
		try {
			return new ActionEraseContentProcessPlatform().execute(password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static boolean eraseContentCms(String password) throws Exception {
		try {
			return new ActionEraseContentCms().execute(password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static boolean eraseContentBbs(String password) throws Exception {
		try {
			return new ActionEraseContentBbs().execute(password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static boolean eraseContentLog(String password) throws Exception {
		try {
			return new ActionEraseContentLog().execute(password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}