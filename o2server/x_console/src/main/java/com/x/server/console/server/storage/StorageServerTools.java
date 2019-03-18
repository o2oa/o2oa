package com.x.server.console.server.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
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

public class StorageServerTools {

	private static Logger logger = LoggerFactory.getLogger(StorageServerTools.class);

	public static FtpServer start(StorageServer storageServer) throws Exception {
		/** 服务器工厂 */
		FtpServerFactory serverFactory = new FtpServerFactory();
		/*** 连接工厂 */
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
		// if (storageServer.getSslEnable()) {
		// File keystoreFile = new File(Config.base(), "config/o2.keystore");
		// SslConfigurationFactory ssl = new SslConfigurationFactory();
		// ssl.setKeystoreFile(keystoreFile);
		// ssl.setKeystorePassword(Config.token().getSsl());
		// listenerFactory.setSslConfiguration(ssl.createSslConfiguration());
		// listenerFactory.setImplicitSsl(true);
		// }
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
		// if (ListTools.isNotEmpty(list)) {
		// return concreteUserManager(list);
		// } else {
		// return concreteDefaultUserManager();
		// }
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

	// private static UserManager concreteDefaultUserManager() throws Exception
	// {
	// List<BaseUser> users = new ArrayList<>();
	// for (StorageType o : StorageType.values()) {
	// BaseUser user = new BaseUser();
	// user.setEnabled(true);
	// user.setName(o.toString());
	// user.setPassword(Config.password());
	// File file = new File(Config.base(), "local/repository/storage/" +
	// o.toString());
	// FileUtils.forceMkdir(file);
	// user.setHomeDirectory(file.getAbsolutePath());
	// user.setMaxIdleTime(0);
	// List<Authority> authorities = new ArrayList<Authority>();
	// authorities.add(new WritePermission());
	// authorities.add(new ConcurrentLoginPermission(0, 0));
	// authorities.add(new TransferRatePermission(0, 0));
	// user.setAuthorities(authorities);
	// users.add(user);
	// }
	// StorageUserManager userManager = new StorageUserManager(users);
	// return userManager;
	// }
}