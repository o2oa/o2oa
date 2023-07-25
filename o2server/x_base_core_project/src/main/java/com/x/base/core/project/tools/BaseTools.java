package com.x.base.core.project.tools;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Node;
import com.x.base.core.project.config.Nodes;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class BaseTools {

	private BaseTools() {
		// nothing
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseTools.class);

	public static String getBasePath() {
		return getBaseDirectory().toAbsolutePath().toString();
	}

	/**
	 * 从Main.class所在的目录开始递归向上,找到version.o2所在目录,就是程序根目录.
	 * 
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private static Path getBaseDirectory() {
		try {
			Path path = Paths.get(
					new URI("file://" + BaseTools.class.getProtectionDomain().getCodeSource().getLocation().getPath()));
			while (Files.exists(path)) {
				Path versionFile = path.resolve("version.o2");
				if (Files.exists(versionFile) && Files.isRegularFile(versionFile)) {
					return path.toAbsolutePath();
				}
				path = path.getParent();
			}
		} catch (URISyntaxException e) {
			throw new UncheckedIOException(new IOException(e));
		}
		throw new UncheckedIOException(new IOException("can not define o2server base directory."));

	}

	public static <T> T readConfigObject(String path, Class<T> cls) {
		String base = BaseTools.getBasePath();
		Path p = Paths.get(base, path);
		if ((!Files.exists(p)) || Files.isDirectory(p)) {
			return null;
		}
		try {
			Gson gson = new Gson();
			JsonElement jsonElement = gson.fromJson(Files.readString(p, StandardCharsets.UTF_8), JsonElement.class);
			if ((null != jsonElement) && jsonElement.isJsonObject()) {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				removeComment(jsonObject);
				return gson.fromJson(jsonObject, cls);
			}
			return gson.fromJson(jsonElement, cls);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static void removeComment(JsonObject jsonObject) {
		jsonObject.entrySet().stream().filter(o -> StringUtils.startsWith(Objects.toString(o.getKey()), "###"))
				.map(Entry::getKey).collect(Collectors.toList()).forEach(jsonObject::remove);
	}

	public static <T> T readConfigObject(String path, String otherPath, Class<T> cls) {
		String base = BaseTools.getBasePath();
		File file = new File(base, path);
		if (file.exists() && file.isFile()) {
			return readConfigObject(path, cls);
		}
		file = new File(base, otherPath);
		if (file.exists() && file.isFile()) {
			return readConfigObject(otherPath, cls);
		}
		throw new UncheckedIOException(
				new IOException("can not get file with path:" + path + ", otherPath:" + otherPath + "."));
	}

	public static void writeObject(String path, Object obj) throws IOException {
		String base = BaseTools.getBasePath();
		File file = new File(base, path);
		String json = (new Gson()).toJson(obj);
		FileUtils.writeStringToFile(file, json, StandardCharsets.UTF_8);
	}

	public static String readCfg(String path) throws IOException {
		String base = BaseTools.getBasePath();
		File file = new File(base, path);
		if ((!file.exists()) || file.isDirectory()) {
			return null;
		}
		String str = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		return (StringUtils.trim(str));
	}

	public static String readCfg(String path, String defaultValue) throws IOException {
		String str = readCfg(path);
		if (StringUtils.isEmpty(str)) {
			str = defaultValue;
		}
		return (StringUtils.trim(str));
	}

	public static void writeCfg(String path, String value) throws IOException {
		String base = BaseTools.getBasePath();
		File file = new File(base, path);
		FileUtils.writeStringToFile(file, StringUtils.trim(value), StandardCharsets.UTF_8);
	}

	public static byte[] readBytes(String path) throws IOException {
		String base = BaseTools.getBasePath();
		File file = new File(base, path);
		if ((!file.exists()) || file.isDirectory()) {
			throw new IOException("can not get file with path:" + file.getAbsolutePath());
		}
		return FileUtils.readFileToByteArray(file);
	}

	public static String readString(String path) throws IOException {
		String base = BaseTools.getBasePath();
		File file = new File(base, path);
		if ((!file.exists()) || file.isDirectory()) {
			return null;
		}
		return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
	}

	public static boolean executeSyncFile(String syncFilePath) throws Exception {
		boolean syncflag = false;
		Nodes nodes = Config.nodes();
		// 同步config文件
		if (BooleanUtils.isTrue(Config.general().getConfigApiEnable())) {
			for (Entry<String, Node> entry : nodes.entrySet()) {
				if (nodes.get(entry.getKey()).getApplication().getEnable()
						|| nodes.get(entry.getKey()).getCenter().getEnable()) {
					syncflag = executeSyncFile(syncFilePath, entry.getKey(), nodes.get(entry.getKey()).nodeAgentPort());
				}
			}
		}
		return syncflag;
	}

	private static boolean executeSyncFile(String syncFilePath, String nodeName, int nodePort) {
		boolean syncFileFlag = false;
		try (Socket socket = new Socket(nodeName, nodePort);
				InputStream fileInputStream = new FileInputStream(new File(Config.base(), syncFilePath));
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				DataInputStream dis = new DataInputStream(socket.getInputStream())) {
			socket.setKeepAlive(true);
			socket.setSoTimeout(2000);
			Map<String, Object> commandObject = new HashMap<>();
			commandObject.put("command", "syncFile:" + syncFilePath);
			commandObject.put("credential", Crypto.rsaEncrypt("o2@", Config.publicKey()));
			dos.writeUTF(XGsonBuilder.toJson(commandObject));
			dos.flush();

			dos.writeUTF(syncFilePath);
			dos.flush();

			LOGGER.info("同步文件:" + syncFilePath + " starting...");
			byte[] bytes = new byte[1024];
			int length = 0;
			while ((length = fileInputStream.read(bytes, 0, bytes.length)) != -1) {
				dos.write(bytes, 0, length);
				dos.flush();
			}
			LOGGER.info("同步文件:" + syncFilePath + " end.");

			syncFileFlag = true;
		} catch (Exception ex) {
			LOGGER.error(ex);
			syncFileFlag = false;
		}
		return syncFileFlag;
	}

	public static String getIpAddress() {
		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = allNetInterfaces.nextElement();
				if (!(netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp())) {
					Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
					while (addresses.hasMoreElements()) {
						ip = addresses.nextElement();
						if (ip instanceof Inet4Address) {
							return ip.getHostAddress();
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return "";
	}
}
