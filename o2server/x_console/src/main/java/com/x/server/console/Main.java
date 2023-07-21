package com.x.server.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.eclipse.jetty.plus.jndi.Resource;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.server.console.log.Log4j2Configuration;

public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	private static final String MANIFEST_FILENAME = "manifest.cfg";
	private static final String GITIGNORE_FILENAME = ".gitignore";
	private static final LinkedBlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();
	private static NodeAgent nodeAgent;

	public static NodeAgent getNodeAgent() {
		return nodeAgent;
	}

	private static void init() throws Exception {
		String base = getBasePath().toString();
		pid(base);
		scanWar(base);
		cleanCommandSwap(base);
		cleanTempDir(base);
		createTempClassesDirectory(base);
		Log4j2Configuration.reconfigure();
		ResourceFactory.init();
		CommandFactory.printStartHelp();
		Hadoop.init();// 初始化hadoop环境
		new Resource(Config.RESOURCE_COMMANDQUEUE, commandQueue);// 注册控制台命令队列,命令队列唯一不可改.
		// 注册 commandTerminatedSignal阻塞队列
		new Resource(Config.RESOURCE_COMMANDTERMINATEDSIGNAL_CTL_RD, new LinkedBlockingQueue<>());
	}

	public static void main(String[] args) throws Exception {
		init();
		if (null == Config.currentNode()) {
			throw new IllegalStateException("无法找到当前节点,请检查config/node_{name}.json与local/node.cfg文件内容中的名称是否一致.");
		}
		CommandThreads.start(commandQueue);
		startNodeAgent();
		// 启动定时任务
		SchedulerBuilder.start();

		if (BooleanUtils.isTrue(Config.currentNode().autoStart())) {
			commandQueue.put("start");
		}
		CommandThreads.join();
	}

	private static void cleanCommandSwap(String base) {
		try (RandomAccessFile raf = new RandomAccessFile(new File(base, "command.swap"), "rw");
				FileChannel channel = raf.getChannel()) {
			FileLock lock = channel.lock(); // 锁定文件
			channel.truncate(0); // 清空文件
			lock.release(); // 释放文件锁
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}

	private static void startNodeAgent() throws Exception {
		if (BooleanUtils.isTrue(Config.currentNode().nodeAgentEnable())) {
			nodeAgent = new NodeAgent();
			nodeAgent.setCommandQueue(commandQueue);
			nodeAgent.setDaemon(true);
			nodeAgent.start();
		}
	}

	private static void createTempClassesDirectory(String base) throws IOException {
		File dir = new File(base, "local/temp/classes");
		FileUtils.forceMkdir(dir);
		FileUtils.cleanDirectory(dir);
	}

	/**
	 * 检查store目录下的war文件是否全部在manifest.cfg中
	 *
	 * @param base o2server的根目录
	 */
	private static void scanWar(String base) throws IOException {
		File dir = new File(base, "store");
		File manifest = new File(dir, MANIFEST_FILENAME);
		if ((!manifest.exists()) || manifest.isDirectory()) {
			LOGGER.print("启动过程忽略扫描 store 目录.");
			return;
		}
		List<String> manifestNames = readManifest(manifest);
		for (File o : dir.listFiles()) {
			if ((o.isDirectory() && o.getName().equals("jars")) || (o.getName().equals(MANIFEST_FILENAME))
					|| (o.getName().equals(GITIGNORE_FILENAME))) {
				continue;
			}
			if (!manifestNames.contains(o.getName())) {
				LOGGER.print("扫描 store 过程中删除无效的文件:{}.", o.getName());
				Files.delete(o.toPath());
			}
		}
	}

	/**
	 * 从Main.class所在的目录开始递归向上,找到version.o2所在目录,就是程序根目录.
	 *
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private static Path getBasePath() throws IOException, URISyntaxException {
		Path path = Paths
				.get(new URI("file://" + Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()));
		while (Files.exists(path)) {
			Path versionFile = path.resolve("version.o2");
			if (Files.exists(versionFile) && Files.isRegularFile(versionFile)) {
				return path.toAbsolutePath();
			}
			path = path.getParent();
		}
		throw new IOException("can not define o2server base directory.");
	}

	private static void cleanTempDir(String base) throws IOException {
		File temp = new File(base, "local/temp");
		FileUtils.forceMkdir(temp);
		FileUtils.cleanDirectory(temp);
	}

	private static List<String> readManifest(File file) throws IOException {
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

	private static void pid(String base) throws IOException {
		RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
		String jvmName = runtimeBean.getName();
		long pid = Long.parseLong(jvmName.split("@")[0]);
		Path path = Paths.get(base, "pid.log");
		Files.write(path, Long.toString(pid).getBytes(), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);
	}
}
