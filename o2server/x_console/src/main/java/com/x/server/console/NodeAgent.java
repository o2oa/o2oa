package com.x.server.console;

import com.google.common.collect.ImmutableList;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.WebServers;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.FileTools;
import com.x.base.core.project.tools.ZipTools;
import com.x.base.core.project.x_base_core_project;
import com.x.server.console.command.Commands;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class NodeAgent extends Thread {

	public NodeAgent() {
		this.setName(NodeAgent.class.getName());
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(NodeAgent.class);

	private static final ReentrantLock lock = new ReentrantLock();

	public static final Pattern redeploy_pattern = Pattern.compile("^redeploy:(.+)$", Pattern.CASE_INSENSITIVE);

	public static final Pattern syncFile_pattern = Pattern.compile("^syncFile:(.+)$", Pattern.CASE_INSENSITIVE);

	public static final Pattern upload_resource_pattern = Pattern.compile("^uploadResource:(.+)$",
			Pattern.CASE_INSENSITIVE);

	public static final Pattern read_log_pattern = Pattern.compile("^readLog:(.+)$", Pattern.CASE_INSENSITIVE);

	public static final Pattern execute_command_pattern = Pattern.compile("^command:(.+)$", Pattern.CASE_INSENSITIVE);

	public static final int LOG_MAX_READ_SIZE = 10 * 1024;

	private static List<String> logLevelList = ImmutableList.of("debug", "info", "warn", "error", "print");

	private LinkedBlockingQueue<String> commandQueue;

	public LinkedBlockingQueue<String> getCommandQueue() {
		return commandQueue;
	}

	public void setCommandQueue(LinkedBlockingQueue<String> commandQueue) {
		this.commandQueue = commandQueue;
	}

	private volatile boolean runFlag = true;

	private ServerSocket serverSocket = null;

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(Config.currentNode().nodeAgentPort());
			Matcher matcher;
			while (runFlag) {
				if (!serverSocket.isClosed()) {
					try (Socket socket = serverSocket.accept()) {
						if (!socket.isClosed()) {
							try (DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
									DataInputStream dis = new DataInputStream(socket.getInputStream())) {
								String json = dis.readUTF();

								CommandObject commandObject = XGsonBuilder.instance().fromJson(json,
										CommandObject.class);
								if (BooleanUtils.isTrue(Config.currentNode().nodeAgentEncrypt())) {
									String decrypt = Crypto.rsaDecrypt(commandObject.getCredential(),
											Config.privateKey());
									if (!StringUtils.startsWith(decrypt, "o2@")) {
										dos.writeUTF("failure:error decrypt!");
										dos.flush();
										continue;
									}
								}

								matcher = syncFile_pattern.matcher(commandObject.getCommand());
								if (matcher.find()) {
									String strCommand = commandObject.getCommand();
									strCommand = strCommand.trim();
									strCommand = strCommand.substring(strCommand.indexOf(":") + 1, strCommand.length());
									LOGGER.info("收接到同步命令:" + strCommand);
									String syncFilePath = dis.readUTF();
									File file = new File(Config.base(), syncFilePath);
									try (FileOutputStream fos = new FileOutputStream(file)) {
										byte[] bytes = new byte[1024];
										int length = 0;
										while ((length = dis.read(bytes, 0, bytes.length)) != -1) {
											fos.write(bytes, 0, length);
											fos.flush();
										}
									}
									Config.flush();
									if (syncFilePath.indexOf("web.json") > -1
											|| syncFilePath.indexOf("collect.json") > -1
											|| syncFilePath.indexOf("portal.json") > -1
											|| syncFilePath.indexOf("person.json") > -1
											|| syncFilePath.indexOf("general.json") > -1
											|| syncFilePath.indexOf("query.json") > -1) {
										// 更新web服务配置信息
										WebServers.updateWebServerConfigJson();
									}
									LOGGER.info("同步完成");
									continue;

								}

								matcher = redeploy_pattern.matcher(commandObject.getCommand());
								if (matcher.find()) {
									String strCommand = commandObject.getCommand().trim();
									strCommand = StringUtils.substringAfter(strCommand, ":");
									LOGGER.info("收接到命令:" + strCommand);
									String filename = dis.readUTF();
									FileUtils.forceMkdir(Config.dir_local_temp());
									File tempFile = new File(Config.dir_local_temp(), filename);
									try (FileOutputStream bos = new FileOutputStream(tempFile)) {
										byte[] onceBytes = new byte[1024];
										int length;
										while ((length = dis.read(onceBytes, 0, onceBytes.length)) != -1) {
											bos.write(onceBytes, 0, length);
											bos.flush();
										}
									}

									filename = filename.substring(0, filename.lastIndexOf("."));
									// 部署
									String result = this.redeploy(strCommand, filename, tempFile);
									LOGGER.info("部署:" + result);
									dos.writeUTF(result);
									dos.flush();
									FileUtils.forceDelete(tempFile);
									continue;
								}

								matcher = upload_resource_pattern.matcher(commandObject.getCommand());
								if (matcher.find()) {
									int fileLength = dis.readInt();
									byte[] bytes;
									try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
										byte[] onceBytes = new byte[1024];
										int length = 0;
										while ((length = dis.read(onceBytes, 0, onceBytes.length)) != -1) {
											bos.write(onceBytes, 0, length);
											bos.flush();
											if (bos.size() == fileLength) {
												break;
											}
										}
										bytes = bos.toByteArray();
									}
									LOGGER.info("receive resource bytes {}", bytes.length);
									String result = this.uploadResource(commandObject.getParam(), bytes);
									dos.writeUTF(result);
									dos.flush();
									continue;
								}

								matcher = read_log_pattern.matcher(commandObject.getCommand());
								if (matcher.find()) {
									long lastTimeFileSize = dis.readLong();
									if (lock.tryLock()) {
										try {
											readLog(lastTimeFileSize, dos);
										} finally {
											lock.unlock();
										}
									} else {
										dos.writeUTF("failure");
										dos.flush();
									}
									continue;
								}

								matcher = execute_command_pattern.matcher(commandObject.getCommand());
								if (matcher.find()) {
									String strCommand = commandObject.getCommand();
									strCommand = strCommand.trim();
									strCommand = strCommand.substring(strCommand.indexOf(":") + 1, strCommand.length());
									LOGGER.info("收接到命令:" + strCommand);
									// 为了同步文件
									commandQueue.add(strCommand);
									continue;
								}

								dos.writeUTF("failure:no pattern method!");
								dos.flush();

							}
						}
					} catch (SocketException se) {
						LOGGER.debug("nodeAgent is closed:{}.", se.getMessage());
					} catch (Exception e) {
						LOGGER.error(e);
					}
				}
				try {
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					LOGGER.error(e);
				}
			}
		}
	}

	public void stopAgent() {
		try {
			this.runFlag = false;
			if (serverSocket != null) {
				serverSocket.close();
				this.serverSocket = null;
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void readLog(long lastTimeFileSize, DataOutputStream dos) throws Exception {
		try {
			File logFile = new File(Config.base(), "logs/out.log");
			if (logFile.exists()) {
				List<Map<String, String>> list = new ArrayList<>();
				try (RandomAccessFile randomFile = new RandomAccessFile(logFile, "r")) {
					long curFileSize = randomFile.length();
					if (lastTimeFileSize <= 0 || lastTimeFileSize > curFileSize) {
						lastTimeFileSize = (curFileSize > LOG_MAX_READ_SIZE) ? (curFileSize - LOG_MAX_READ_SIZE) : 0;
					}
					randomFile.seek(lastTimeFileSize);
					int curReadSize = 0;
					String tmp = "";
					String curTime = "2020-01-01 00:00:01.001";
					while ((tmp = randomFile.readLine()) != null) {
						byte[] bytes = tmp.getBytes(StandardCharsets.ISO_8859_1.name());
						curReadSize = curReadSize + bytes.length + 1;
						String lineStr = new String(bytes, DefaultCharset.name);
						String time = curTime;
						String logLevel = "";
						if (lineStr.length() > 0) {
							if (lineStr.length() > 23) {
								String arr[] = lineStr.split(" ");
								if (arr.length > 3) {
									time = arr[0] + " " + arr[1];
									if (time.length() > 19
											&& BooleanUtils.isTrue(DateTools.isDateTime(StringUtils.left(time, 19)))) {
										curTime = time;
										if (logLevelList.contains(arr[3].toLowerCase())) {
											logLevel = arr[3];
										}
									} else {
										time = curTime;
									}
								}
							}
						} else {
							if (curReadSize > LOG_MAX_READ_SIZE) {
								break;
							} else {
								continue;
							}
						}
						Map<String, String> map = new HashMap<>();
						map.put("logTime", time + "#" + Config.node());
						map.put("node", Config.node());
						map.put("logLevel", logLevel);
						map.put("lineLog", lineStr);
						list.add(map);
						if (curReadSize > LOG_MAX_READ_SIZE) {
							break;
						}
					}
					if (curReadSize > 0) {
						lastTimeFileSize = lastTimeFileSize + curReadSize;
					}
				}
				dos.writeUTF(XGsonBuilder.toJson(list));
				dos.flush();

				dos.writeLong(lastTimeFileSize);
				dos.flush();

				return;
			}
		} catch (Exception e) {
			LOGGER.print("readLog error:{}", e.getMessage());
		}
		dos.writeUTF("failure");
		dos.flush();
	}

	private String uploadResource(Map<String, Object> param, byte[] bytes) {
		String result = "success";
		if (param == null || param.isEmpty()) {
			result = "failure";
			return result;
		}
		try {
			String fileName = (String) param.get("fileName");
			String filePath = (String) param.get("filePath");
			Boolean flag = (Boolean) param.get("asNew");
			boolean asNew = flag == null ? false : flag;

			if (StringUtils.isNotEmpty(fileName)) {
				if (fileName.toLowerCase().endsWith(".zip")) {
					File tempFile = new File(Config.base(), "local/temp/upload");
					FileTools.forceMkdir(tempFile);
					FileUtils.cleanDirectory(tempFile);

					File zipFile = new File(tempFile.getAbsolutePath(), fileName);
					FileUtils.writeByteArrayToFile(zipFile, bytes);
					File dist = Config.path_webroot(true).toFile();
					File dist2 = Config.dir_servers_webServer();
					List<String> ignoreList = new ArrayList<>();
					ZipTools.unZip(zipFile, ignoreList, dist, WebServers.WEB_SERVER_FOLDERS, dist2, asNew, null);

					FileUtils.cleanDirectory(tempFile);
					LOGGER.print("upload resource {} success!", fileName);
				} else if (StringUtils.isNotEmpty(filePath)) {
					filePath = filePath.trim();
					File dist = Config.path_webroot(true).toFile();
					if (ZipTools.isMember(filePath, WebServers.WEB_SERVER_FOLDERS)) {
						dist = Config.dir_servers_webServer();
					}
					dist = new File(dist, filePath);
					FileTools.forceMkdir(dist);
					File file = new File(dist, fileName);
					Files.deleteIfExists(file.toPath());
					FileUtils.writeByteArrayToFile(file, bytes);
					LOGGER.print("upload resource {} success!", fileName);
				} else {
					result = "failure";
				}
			} else {
				result = "failure";
			}

		} catch (Exception e) {
			LOGGER.print("upload resource {} error={}", XGsonBuilder.toJson(param), e.getMessage());
			result = "failure";
		}
		return result;
	}

	private String redeploy(String type, String name, File tempFile) {
		String result = "success";
		try {
			LOGGER.print("redeploy:{}.", name);
			switch (type) {
			case "storeWar":
				storeWar(name, tempFile);
				break;
			case "storeJar":
				storeJar(name, tempFile);
				break;
			case "customWar":
				customWar(name, tempFile);
				break;
			case "customJar":
				customJar(name, tempFile);
				break;
			case "customZip":
				customZip(name, tempFile);
				break;
			case "serverZip":
				serverZip(name, tempFile);
				break;
			}
		} catch (Exception e) {
			LOGGER.error(e);
			result = e.getMessage();
		}
		return result;
	}

	private void storeWar(String simpleName, File tempFile) throws Exception {
		File war = new File(Config.dir_store(), simpleName + ".war");
		FileUtils.copyFile(tempFile, war);
	}

	private void storeJar(String simpleName, File tempFile) throws Exception {
		File jar = new File(Config.dir_store_jars(true), simpleName + ".jar");
		FileUtils.copyFile(tempFile, jar);
	}

	private void customWar(String simpleName, File tempFile) throws Exception {
		File war = new File(Config.dir_custom(true), simpleName + ".war");
		FileUtils.copyFile(tempFile, war);
	}

	private void customJar(String simpleName, File tempFile) throws Exception {
		File jar = new File(Config.dir_custom_jars(true), simpleName + ".jar");
		FileUtils.copyFile(tempFile, jar);
	}

	private void customZip(String simpleName, File zipFile) throws Exception {
		LOGGER.print("start deploy customZip app {} ", simpleName);
		File dist = Config.dir_custom(true);
		List<String> subs = new ArrayList<>();
		ZipTools.unZip(zipFile, subs, dist, false, null);
	}

	private void serverZip(String simpleName, File tempFile) throws Exception {
		LOGGER.print("start deploy serverZip {} ", simpleName);
		File upFile = new File(Config.base(), "local/temp/deploy");
		FileTools.forceMkdir(upFile);

		File zipFile = new File(upFile.getAbsolutePath(), simpleName + ".zip");
		FileUtils.copyFile(tempFile, zipFile);
		Commands.execute("ctl -uf " + zipFile.getAbsolutePath());
	}

	protected static String calculateExtraClassPath(Class<?> cls, Path... paths) throws Exception {
		List<String> jars = new ArrayList<>();
		jars.addAll(calculateExtraClassPathDefault());
		Module module = cls.getAnnotation(Module.class);
		for (String str : module.storeJars()) {
			File file = new File(Config.dir_store_jars(), str + ".jar");
			if (file.exists()) {
				jars.add(file.getAbsolutePath());
			}
		}
		for (String str : module.customJars()) {
			File file = new File(Config.dir_custom_jars(), str + ".jar");
			if (file.exists()) {
				jars.add(file.getAbsolutePath());
			}
		}
		for (Path path : paths) {
			if (Files.exists(path) && Files.isDirectory(path)) {
				try (Stream<Path> stream = Files.walk(path, FileVisitOption.FOLLOW_LINKS)) {
					stream.filter(Files::isRegularFile)
							.filter(p -> p.toAbsolutePath().toString().toLowerCase().endsWith(".jar"))
							.forEach(p -> jars.add(p.toAbsolutePath().toString()));
				}
			}
		}
		return StringUtils.join(jars, ";");
	}

	private static List<String> calculateExtraClassPathDefault() throws Exception {
		List<String> jars = new ArrayList<>();
		IOFileFilter filter = new WildcardFileFilter(x_base_core_project.class.getSimpleName() + "*.jar");
		for (File o : FileUtils.listFiles(Config.dir_store_jars(), filter, null)) {
			jars.add(o.getAbsolutePath());
		}
		filter = new WildcardFileFilter("openjpa-*.jar");
		/* 如果不单独导入会导致java.lang.NoClassDefFoundError: org/eclipse/jetty/http/MimeTypes */
		filter = FileFilterUtils.or(filter, new WildcardFileFilter("jetty-all-*.jar"));
		filter = FileFilterUtils.or(filter, new WildcardFileFilter("quartz-*.jar"));
		filter = FileFilterUtils.or(filter, new WildcardFileFilter("slf4j-simple-*.jar"));
		filter = FileFilterUtils.or(filter, new WildcardFileFilter("jul-to-slf4j-*.jar"));
		filter = FileFilterUtils.or(filter, new WildcardFileFilter("log4j-*.jar"));
		/* jersey从AppClassLoader加载 */
		for (File o : FileUtils.listFiles(Config.pathCommonsExt(true).toFile(), filter, null)) {
			jars.add(o.getAbsolutePath());
		}
		return jars;
	}

	public static class CommandObject {

		private String command;

		private String body;

		private String credential;

		private Map<String, Object> param;

		public String getCommand() {
			return command;
		}

		public void setCommand(String command) {
			this.command = command;
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}

		public String getCredential() {
			return credential;
		}

		public void setCredential(String credential) {
			this.credential = credential;
		}

		public Map<String, Object> getParam() {
			return param;
		}

		public void setParam(Map<String, Object> param) {
			this.param = param;
		}

	}

//	private String type(String simpleName) throws Exception {
//		if ((new File(Config.dir_store(), simpleName + ".war")).exists()) {
//			return "storeWar";
//		}
//		if ((new File(Config.dir_store_jars(), simpleName + ".jar")).exists()) {
//			return "storeJar";
//		}
//		if ((new File(Config.dir_custom(), simpleName + ".war")).exists()) {
//			return "customWar";
//		}
//		if ((new File(Config.dir_custom_jars(), simpleName + ".jar")).exists()) {
//			return "customJar";
//		}
//		return null;
//	}

}
