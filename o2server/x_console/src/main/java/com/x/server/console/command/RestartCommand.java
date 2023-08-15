package com.x.server.console.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.server.console.CommandThreads;

public class RestartCommand extends StopCommand {

	private static final String PATTERN_TEXT = "^ {0,}restart {0,}$";
	public static final Pattern PATTERN = Pattern.compile(PATTERN_TEXT, Pattern.CASE_INSENSITIVE);

	private static final Consumer<Matcher> consumer = matcher -> restart();

	public static Consumer<Matcher> consumer() {
		return consumer;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(StartCommand.class);

	private static void restart() {
		try {
			LOGGER.print("ready to restart...");
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
				LOGGER.print("server will start in new process!");
				Runtime.getRuntime().exec(file.getAbsolutePath());
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
				LOGGER.print("not support restart in current operating system!start server failure!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	private static void stopAllThreads() {
		CommandThreads.stop();
	}

}
