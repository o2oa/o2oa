package com.x.server.console;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.Crypto;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Scheduler;

import com.x.base.core.project.config.ApplicationServer;
import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DataServer;
import com.x.base.core.project.config.StorageServer;
import com.x.base.core.project.config.WebServer;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.StringTools;
import com.x.server.console.action.ActionConfig;
import com.x.server.console.action.ActionControl;
import com.x.server.console.action.ActionCreateEncryptKey;
import com.x.server.console.action.ActionSetPassword;
import com.x.server.console.action.ActionVersion;
import com.x.server.console.log.LogTools;
import com.x.server.console.server.Servers;

public class Main {

	private static final String MANIFEST_FILENAME = "manifest.cfg";
	private static final String GITIGNORE_FILENAME = ".gitignore";
	private static final LinkedBlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();
	private static NodeAgent nodeAgent;

	private static final Thread swapCommandThread = new Thread(() -> {
		// 文件中的命令输出到解析器
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
					commandQueue.put(cmd);
					continue;
				}
				Thread.sleep(1500);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}, "swapCommandThread");

	private static final Thread consoleCommandThread = new Thread(() -> {
		// 将屏幕命令输出到解析器
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			String cmd = "";
			while (null != cmd) {
				cmd = reader.readLine();
				// 在linux环境中当前端console窗口关闭后会导致可以立即read到一个null的input值
				if (null != cmd) {
					commandQueue.put(cmd);
					continue;
				}
				Thread.sleep(5000);
			}
		} catch (Exception e) {
			System.out.println("console input closed!");
		}
	}, "consoleCommandThread");

	private static void init() throws Exception {
		String base = getBasePath();
		pid(base);
		scanWar(base);
		cleanTempDir(base);
		createTempClassesDirectory(base);
		SystemOutErrorSideCopyBuilder.start();
		LogTools.setSlf4jSimple();
		ResourceFactory.bind();
		CommandFactory.printStartHelp();
	}

	public static void main(String[] args) throws Exception {
		init();
		if (null == Config.currentNode()) {
			throw new Exception("无法找到当前节点,请检查config/node_{name}.json与local/node.cfg文件内容中的名称是否一致.");
		}
		swapCommandThread.start();
		consoleCommandThread.start();
		if (BooleanUtils.isTrue(Config.currentNode().nodeAgentEnable())) {
			nodeAgent = new NodeAgent();
			nodeAgent.setCommandQueue(commandQueue);
			nodeAgent.setDaemon(true);
			nodeAgent.start();
		}

		SchedulerBuilder schedulerBuilder = new SchedulerBuilder();
		Scheduler scheduler = schedulerBuilder.start();

		if (BooleanUtils.isTrue(Config.currentNode().autoStart())) {
			startAll();
		}

		Matcher matcher = null;
		String cmd = "";
		while (true) {
			try {
				cmd = commandQueue.take();
			} catch (Exception e) {
				e.printStackTrace();
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

			matcher = CommandFactory.setPassword_pattern.matcher(cmd);
			if (matcher.find()) {
				setPassword(matcher.group(1), matcher.group(2));
				if (config()) {
					break;
				} else {
					continue;
				}
			}

			matcher = CommandFactory.create_encrypt_key_pattern.matcher(cmd);
			if (matcher.find()) {
				createEncryptKey();
				continue;
			}

			matcher = CommandFactory.control_pattern.matcher(cmd);
			if (matcher.find()) {
				control(cmd);
				continue;
			}

			matcher = CommandFactory.exit_pattern.matcher(cmd);
			if (matcher.find()) {
				exit();
			}

			matcher = CommandFactory.restart_pattern.matcher(cmd);
			if (matcher.find()) {
				restart();
				continue;
			}
			System.out.println("unknown command:" + cmd);
		}
		// 关闭定时器
		scheduler.shutdown();
		SystemOutErrorSideCopyBuilder.stop();
	}

	private static boolean createEncryptKey() {
		try {
			return new ActionCreateEncryptKey().execute();
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
			if (BooleanUtils.isTrue(Servers.dataServerIsRunning())) {
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
			if (BooleanUtils.isFalse(Servers.dataServerIsRunning())) {
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
			if (BooleanUtils.isTrue(Servers.storageServerIsRunning())) {
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
			if (BooleanUtils.isFalse(Servers.storageServerIsRunning())) {
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
			if (BooleanUtils.isTrue(Servers.applicationServerIsRunning())) {
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
			if (BooleanUtils.isFalse(Servers.applicationServerIsRunning())) {
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
			if (BooleanUtils.isTrue(Servers.centerServerIsRunning())) {
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
			if (BooleanUtils.isFalse(Servers.centerServerIsRunning())) {
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
			if (BooleanUtils.isTrue(Servers.webServerIsRunning())) {
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
			if (BooleanUtils.isFalse(Servers.webServerIsRunning())) {
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
			if ((null != dataServer) && (BooleanUtils.isTrue(dataServer.getEnable()))) {
				startDataServer();
			}

			StorageServer storageServer = Config.currentNode().getStorage();
			if ((null != storageServer) && (BooleanUtils.isTrue(storageServer.getEnable()))) {
				startStorageServer();
			}

			CenterServer centerServer = Config.currentNode().getCenter();
			if ((null != centerServer) && (BooleanUtils.isTrue(centerServer.getEnable()))) {
				startCenterServer();
			}
			ApplicationServer applicationServer = Config.currentNode().getApplication();
			if ((null != applicationServer) && (BooleanUtils.isTrue(applicationServer.getEnable()))) {
				startApplicationServer();
			}
			WebServer webServer = Config.currentNode().getWeb();
			if ((null != webServer) && (BooleanUtils.isTrue(webServer.getEnable()))) {
				startWebServer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void exit() {
		stopAll();
		System.exit(0);
	}

	private static void restart() {
		try {
			System.out.println("ready to restart...");
			stopAll();
			stopAllThreads();
			String osName = System.getProperty("os.name");
			// System.out.println("当前操作系统是："+osName);
			File file = new File(Config.base(), "start_linux.sh");
			if (osName.toLowerCase().startsWith("mac")) {
				file = new File(Config.base(), "start_macos.sh");
			} else if (osName.toLowerCase().startsWith("windows")) {
				file = new File(Config.base(), "start_windows.bat");
			} else if (!file.exists()) {
				file = new File("start_aix.sh");
				if (!file.exists()) {
					file = new File("start_arm.sh");
					if (!file.exists()) {
						file = new File("start_mips.sh");
						if (!file.exists()) {
							file = new File("start_raspi.sh");
						}
					}
				}
			}
			if (file.exists()) {
				System.out.println("server will start in new process!");
				Process ps = Runtime.getRuntime().exec(file.getAbsolutePath());
				Thread.sleep(2000);
				if (!Config.currentNode().autoStart()) {
					for (int i = 0; i < 5; i++) {
						try (Socket socket = new Socket(Config.node(), Config.currentNode().nodeAgentPort())) {
							socket.setKeepAlive(true);
							socket.setSoTimeout(2000);
							try (DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
									DataInputStream dis = new DataInputStream(socket.getInputStream())) {
								Map<String, Object> commandObject = new HashMap<>();
								commandObject.put("command", "command:start");
								commandObject.put("credential", Crypto.rsaEncrypt("o2@", Config.publicKey()));
								dos.writeUTF(XGsonBuilder.toJson(commandObject));
								dos.flush();
								break;
							}
						} catch (Exception ex) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
							}
						}
					}
				}
			} else {
				System.out.println("not support restart in current operating system!start server failure!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	private static void stopAllThreads() {
		if (swapCommandThread != null) {
			try {
				swapCommandThread.interrupt();
			} catch (Exception e) {
			}
		}
		if (consoleCommandThread != null) {
			try {
				consoleCommandThread.interrupt();
			} catch (Exception e) {
			}
		}
		if (nodeAgent != null) {
			try {
				nodeAgent.stopAgent();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				nodeAgent.interrupt();
				nodeAgent = null;
			} catch (Exception e) {
			}
		}

	}

	private static void stopAll() {
		try {
			WebServer webServer = Config.currentNode().getWeb();
			if ((null != webServer) && (BooleanUtils.isTrue(webServer.getEnable()))) {
				stopWebServer();
			}
			ApplicationServer applicationServer = Config.currentNode().getApplication();
			if ((null != applicationServer) && (BooleanUtils.isTrue(applicationServer.getEnable()))) {
				stopApplicationServer();
			}
			if (BooleanUtils.isTrue(Config.currentNode().getIsPrimaryCenter())) {
				stopCenterServer();
			}
			StorageServer storageServer = Config.currentNode().getStorage();
			if ((null != storageServer) && (BooleanUtils.isTrue(storageServer.getEnable()))) {
				stopStorageServer();
			}
			DataServer dataServer = Config.currentNode().getData();
			if ((null != dataServer) && (BooleanUtils.isTrue(dataServer.getEnable()))) {
				stopDataServer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void control(String cmd) {
		try {
			String[] args = StringTools.translateCommandline(cmd);
			args = Arrays.copyOfRange(args, 1, args.length);
			ActionControl action = new ActionControl();
			action.execute(args);
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
				if (StringUtils.equalsIgnoreCase("jar", FilenameUtils.getExtension(file.getName()))) {
					method.invoke(urlClassLoader, new Object[] { file.toURI().toURL() });
				}
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

	private static void pid(String base) throws IOException {
		RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
		String jvmName = runtimeBean.getName();
		long pid = Long.parseLong(jvmName.split("@")[0]);
		Path path = Paths.get(base, "pid.log");
		Files.write(path, Long.toString(pid).getBytes(), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);
	}
}
