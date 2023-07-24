package com.x.server.console.server.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageServer;
import com.x.base.core.project.config.StorageServer.Account;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.server.console.server.Servers;

public class StorageServerTools {

	private static final Logger LOGGER = LoggerFactory.getLogger(StorageServerTools.class);

	private StorageServerTools() {
		// nothing
	}

	public static FtpServer start() throws Exception {

		StorageServer storageServer = Config.currentNode().getStorage();

		if (null == storageServer) {
			LOGGER.info("storagae server is not configured.");
			return null;
		} else if (BooleanUtils.isNotTrue(storageServer.getEnable())) {
			LOGGER.info("storagae server is not enable.");
			return null;
		}

		FtpServerFactory serverFactory = new FtpServerFactory();
		ConnectionConfigFactory connectionConfigFactory = new ConnectionConfigFactory();
		connectionConfigFactory.setAnonymousLoginEnabled(false);
		connectionConfigFactory.setMaxLogins(1000);
		connectionConfigFactory.setMaxThreads(1000);
		/** 监听工厂 */
		ListenerFactory listenerFactory = new ListenerFactory();
		/** 数据传输工厂,在监听工厂使用 */
		DataConnectionConfigurationFactory dataConnectionConfigurationFactory = new DataConnectionConfigurationFactory();
		/**
		 * 如果不指定端口会WARN:<br/>
		 * <p>
		 * WARN org.apache.ftpserver.impl.PassivePorts - Releasing unreserved passive
		 * port: 41662
		 * </p>
		 */
		dataConnectionConfigurationFactory.setPassivePorts(storageServer.getPassivePorts());
		// /**强制不使用ip检查?不知道啥意思*/
		dataConnectionConfigurationFactory.setPassiveIpCheck(false);
		listenerFactory
				.setDataConnectionConfiguration(dataConnectionConfigurationFactory.createDataConnectionConfiguration());
		listenerFactory.setPort(storageServer.getPort());
		Listener listener = listenerFactory.createListener();
		serverFactory.addListener("default", listener);
		serverFactory.setConnectionConfig(connectionConfigFactory.createConnectionConfig());
		serverFactory.setUserManager(calculateUserManager(storageServer.getCalculatedAccounts()));
		FtpServer server = serverFactory.createServer();
		server.start();
		System.out.println("****************************************");
		System.out.println("* storage server start completed.");
		System.out.println("* port: " + storageServer.getPort() + ".");
		System.out.println("****************************************");
		return server;
	}

	public static void stop(FtpServer server) throws Exception {
		if ((server != null) && (!server.isStopped())) {
			server.stop();
		}
	}

	private static UserManager calculateUserManager(List<Account> list) throws Exception {
		return concreteUserManager(list);
	}

	private static UserManager concreteUserManager(List<Account> list) throws Exception {
		List<BaseUser> users = new ArrayList<>();
		for (Account o : list) {
			BaseUser user = new BaseUser();
			user.setEnabled(true);
			user.setName(o.getUsername());
			String password = o.getPassword();
			if (StringUtils.isEmpty(password)) {
				password = Config.token().getPassword();
			}
			user.setPassword(password);
			File file = new File(Config.base(), "local/repository/storage/" + o.getUsername());
			FileUtils.forceMkdir(file);
			user.setHomeDirectory(file.getAbsolutePath());
			user.setMaxIdleTime(0);
			List<Authority> authorities = new ArrayList<Authority>();
			authorities.add(new WritePermission());
			authorities.add(new ConcurrentLoginPermission(0, 0));
			authorities.add(new TransferRatePermission(0, 0));
			user.setAuthorities(authorities);
			users.add(user);
		}
		StorageUserManager userManager = new StorageUserManager(users);
		return userManager;
	}

}