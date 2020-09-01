package com.x.base.core.project.config;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class ConfigObject extends GsonPropertyObject {
    private static Logger logger = LoggerFactory.getLogger(ConfigObject.class);

    public boolean executeSyncFile(String syncFilePath) throws Exception {

        boolean Syncflag = false;
        String localip = getIpAddress();
        Nodes nodes = Config.nodes();
        //同步config文件
        for (String node : nodes.keySet()) {
            //其他服务器
            if (!node.equalsIgnoreCase(localip)) {
                if (nodes.get(node).getApplication().getEnable() || nodes.get(node).getCenter().getEnable()) {
                     Syncflag = this.executeSyncFile(syncFilePath, node, nodes.get(node).nodeAgentPort());
                }
            }
        }
        return  Syncflag;
    }

    private boolean executeSyncFile(String syncFilePath , String nodeName ,int nodePort){
        boolean syncFileFlag = false;
        File syncFile;
        InputStream fileInputStream = null;

        try (Socket socket = new Socket(nodeName, nodePort)) {

            syncFile = new File(Config.base(), syncFilePath);
            fileInputStream= new FileInputStream(syncFile);

            socket.setKeepAlive(true);
            socket.setSoTimeout(5000);
            DataOutputStream dos = null;
            DataInputStream dis  = null;
            try {
                dos = new DataOutputStream(socket.getOutputStream());
                dis = new DataInputStream(socket.getInputStream());

                Map<String, Object> commandObject = new HashMap<>();
                commandObject.put("command", "syncFile:"+ syncFilePath);
                commandObject.put("credential", Crypto.rsaEncrypt("o2@", Config.publicKey()));
                dos.writeUTF(XGsonBuilder.toJson(commandObject));
                dos.flush();

                dos.writeUTF(syncFilePath);
                dos.flush();


                logger.info("同步文件:"+syncFilePath+" starting...");
                byte[] bytes = new byte[1024];
                int length =0;
                while((length = fileInputStream.read(bytes, 0, bytes.length)) != -1) {
                    dos.write(bytes, 0, length);
                    dos.flush();
                }
                logger.info("同步文件:" + syncFilePath +"end.");

            }finally {
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
