package com.x.base.core.project.tools;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Nodes;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class BaseTools {
    private static Logger logger = LoggerFactory.getLogger(BaseTools.class);

    public static String getBasePath() throws IOException, URISyntaxException {
        return getBaseDirectory().toAbsolutePath().toString();
    }

    /**
     * 从Main.class所在的目录开始递归向上,找到version.o2所在目录,就是程序根目录.
     * 
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    private static Path getBaseDirectory() throws IOException, URISyntaxException {
        Path path = Paths.get(
                new URI("file://" + BaseTools.class.getProtectionDomain().getCodeSource().getLocation().getPath()));
        while (Files.exists(path)) {
            Path versionFile = path.resolve("version.o2");
            if (Files.exists(versionFile) && Files.isRegularFile(versionFile)) {
                return path.toAbsolutePath();
            }
            path = path.getParent();
        }
        throw new IOException("can not define o2server base directory.");
    }

//	public static File getBaseDirectory() throws Exception {
//		String path = BaseTools.class.getProtectionDomain().getCodeSource().getLocation().getPath();
//		File file = new File(path);
//		if (!file.isDirectory()) {
//			file = file.getParentFile();
//		}
//		while (null != file) {
//			File versionFile = new File(file, "version.o2");
//			if (versionFile.exists()) {
//				return file;
//			}
//			file = file.getParentFile();
//		}
//		throw new Exception("can not define o2server base directory.");
//	}

    public static <T> T readConfigObject(String path, Class<T> cls) throws Exception {
        String base = BaseTools.getBasePath();
        File file = new File(base, path);
        if ((!file.exists()) || file.isDirectory()) {
            return null;
        }
        String json = FileUtils.readFileToString(file, DefaultCharset.charset);

        Gson gson = new Gson();

        JsonElement jsonElement = gson.fromJson(json, JsonElement.class);
        if ((null != jsonElement) && jsonElement.isJsonObject()) {
            LinkedHashMap<Object, Object> map = new LinkedHashMap<>();
            map = new Gson().fromJson(jsonElement, map.getClass());
            removeComment(map);
            jsonElement = gson.toJsonTree(map);
        }
        return gson.fromJson(jsonElement, cls);
    }

    private static void removeComment(Map<Object, Object> map) {
        List<Entry<Object, Object>> entries = new ArrayList<>();
        for (Entry<Object, Object> entry : map.entrySet()) {
            if (StringUtils.startsWith(Objects.toString(entry.getKey()), "###")) {
                entries.add(entry);
                continue;
            } else {
                if (entry.getValue() instanceof Map) {
                    removeComment((Map<Object, Object>) entry.getValue());
                }
            }
        }
        for (Entry<Object, Object> entry : entries) {
            map.remove(entry.getKey());
        }
    }

    public static <T> T readConfigObject(String path, String otherPath, Class<T> cls) throws Exception {
        String base = BaseTools.getBasePath();
        File file = new File(base, path);
        if (file.exists() && file.isFile()) {
            return readConfigObject(path, cls);
        }
        file = new File(base, otherPath);
        if (file.exists() && file.isFile()) {
            return readConfigObject(otherPath, cls);
        }
        throw new Exception("can not get file with path:" + path + ", otherPath:" + otherPath + ".");
    }

    public static void writeObject(String path, Object obj) throws Exception {
        String base = BaseTools.getBasePath();
        File file = new File(base, path);
        String json = (new Gson()).toJson(obj);
        FileUtils.writeStringToFile(file, json, DefaultCharset.charset);
    }

    public static String readCfg(String path) throws Exception {
        String base = BaseTools.getBasePath();
        File file = new File(base, path);
        if ((!file.exists()) || file.isDirectory()) {
            return null;
        }
        String str = FileUtils.readFileToString(file, DefaultCharset.charset);
        return (StringUtils.trim(str));
    }

    public static String readCfg(String path, String defaultValue) throws Exception {
        String str = readCfg(path);
        if (StringUtils.isEmpty(str)) {
            str = defaultValue;
        }
        return (StringUtils.trim(str));
    }

    public static void writeCfg(String path, String value) throws Exception {
        String base = BaseTools.getBasePath();
        File file = new File(base, path);
        FileUtils.writeStringToFile(file, StringUtils.trim(value), DefaultCharset.charset);
    }

    public static byte[] readBytes(String path) throws IOException, URISyntaxException {
        String base = BaseTools.getBasePath();
        File file = new File(base, path);
        if ((!file.exists()) || file.isDirectory()) {
            throw new IOException("can not get file with path:" + file.getAbsolutePath());
        }
        return FileUtils.readFileToByteArray(file);
    }

    public static String readString(String path) throws IOException, URISyntaxException {
        String base = BaseTools.getBasePath();
        File file = new File(base, path);
        if ((!file.exists()) || file.isDirectory()) {
            return null;
        }
        return FileUtils.readFileToString(file, DefaultCharset.charset);
    }

    public static boolean executeSyncFile(String syncFilePath) throws Exception {

        boolean Syncflag = false;
        Nodes nodes = Config.nodes();
        // 同步config文件
        if (BooleanUtils.isTrue(Config.general().getConfigApiEnable())) {
            for (String node : nodes.keySet()) {
                if (nodes.get(node).getApplication().getEnable() || nodes.get(node).getCenter().getEnable()) {
                    Syncflag = executeSyncFile(syncFilePath, node, nodes.get(node).nodeAgentPort());
                }
            }
        }
        return Syncflag;
    }

    private static boolean executeSyncFile(String syncFilePath, String nodeName, int nodePort) {
        boolean syncFileFlag = false;
        File syncFile;
        InputStream fileInputStream = null;

        try (Socket socket = new Socket(nodeName, nodePort)) {

            syncFile = new File(Config.base(), syncFilePath);
            fileInputStream = new FileInputStream(syncFile);

            socket.setKeepAlive(true);
            socket.setSoTimeout(2000);
            DataOutputStream dos = null;
            DataInputStream dis = null;
            try {
                dos = new DataOutputStream(socket.getOutputStream());
                dis = new DataInputStream(socket.getInputStream());

                Map<String, Object> commandObject = new HashMap<>();
                commandObject.put("command", "syncFile:" + syncFilePath);
                commandObject.put("credential", Crypto.rsaEncrypt("o2@", Config.publicKey()));
                dos.writeUTF(XGsonBuilder.toJson(commandObject));
                dos.flush();

                dos.writeUTF(syncFilePath);
                dos.flush();

                logger.info("同步文件:" + syncFilePath + " starting...");
                byte[] bytes = new byte[1024];
                int length = 0;
                while ((length = fileInputStream.read(bytes, 0, bytes.length)) != -1) {
                    dos.write(bytes, 0, length);
                    dos.flush();
                }
                logger.info("同步文件:" + syncFilePath + " end.");

            } finally {
                dos.close();
                dis.close();
                socket.close();
                fileInputStream.close();
            }

            syncFileFlag = true;
        } catch (Exception ex) {
            logger.error(ex);
            syncFileFlag = false;
        }
        return syncFileFlag;
    }

    public static String getIpAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("IP地址获取失败" + e.toString());
        }
        return "";
    }
}
