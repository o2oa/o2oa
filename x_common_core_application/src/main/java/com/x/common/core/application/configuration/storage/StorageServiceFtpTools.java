package com.x.common.core.application.configuration.storage;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import com.x.base.core.utils.net.Host;

public class StorageServiceFtpTools {

	public static FTPClient initClient(StorageMapping storageMapping) throws Exception {
		return initFtpClient(storageMapping.getFtpHost(), storageMapping.getFtpPort(), storageMapping.getFtpUsername(),
				storageMapping.getFtpPassword(), storageMapping.getFtpPath());
	}

	public static FTPClient initFtpClient(String host, Integer port, String username, String password, String path)
			throws Exception {
		FTPClient client = new FTPClient();
		client.setControlEncoding("UTF-8");
		client.connect((StringUtils.isNotEmpty(host) ? host : Host.ROLLBACK_IPV4), port);
		if (!client.login(username, password)) {
			throw new Exception("can not login server," + host + ":" + port);
		}
		client.enterLocalPassiveMode();
		client.setFileType(FTP.BINARY_FILE_TYPE);
		if (StringUtils.isNotEmpty(path)) {
			String[] dirs = StringUtils.split(path, "/");
			StorageServiceFtpTools.changeToDirectory(client, dirs);
		}
		return client;
	}

	public static void changeToDirectory(FTPClient client, String... dirs) throws IOException, Exception {
		for (String dir : dirs) {
			if (!client.changeWorkingDirectory(dir)) {
				if (!client.makeDirectory(dir)) {
					throw new Exception("can not create directory:" + dir + ".");
				}
				if (!client.changeWorkingDirectory(dir)) {
					throw new Exception("can not enter directory:" + dir + ".");
				}
			}
		}
	}

	public static void close(FTPClient client) {
		try {
			if (null != client) {
				if (client.isConnected()) {
					client.disconnect();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
