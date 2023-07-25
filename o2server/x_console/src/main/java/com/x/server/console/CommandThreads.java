package com.x.server.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.server.console.command.Commands;

public class CommandThreads {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommandThreads.class);

	private static volatile boolean isRunning = true;

	private static Thread commandReadThread;
	private static Thread commandExecuteThread;

	public static void join() {
		try {
			commandReadThread.join();
			commandExecuteThread.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			LOGGER.print(e.getMessage());
		}
	}

	private CommandThreads() {
		// nothing
	}

	public static void start(LinkedBlockingQueue<String> commandQueue) {
		isRunning = true;
		commandReadThread = createCommandReadThread(commandQueue);
		commandExecuteThread = createCommandExecuteThread(commandQueue);
		commandReadThread.start();
		commandExecuteThread.start();
	}

	public static void stop() {
		isRunning = false;
		if (null != commandReadThread) {
			commandReadThread.interrupt();
		}
		if (null != commandExecuteThread) {
			commandExecuteThread.interrupt();
		}
	}

	private static Thread createCommandReadThread(LinkedBlockingQueue<String> commandQueue) {
		return new Thread(() -> {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
				while (isRunning) {
					fromSystemIn(commandQueue, reader);
					fromFile(commandQueue);
					Thread.sleep(2000);
				}
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, "commandFromThread");
	}

	/**
	 * 读取本地文件中的内容并写入commandQueue
	 * 
	 * @param commandQueue
	 * @return
	 * @throws InterruptedException
	 */
	private static void fromFile(LinkedBlockingQueue<String> commandQueue) throws InterruptedException {
		try (RandomAccessFile file = new RandomAccessFile(Config.base() + "/command.swap", "rw");
				FileChannel channel = file.getChannel()) {
			FileLock lock = channel.lock(); // 锁定文件
			if (file.length() > 0) {
				ByteBuffer buffer = ByteBuffer.allocate((int) file.length());
				channel.read(buffer);
				buffer.flip();
				commandQueue.put(new String(buffer.array(), StandardCharsets.UTF_8));
				file.setLength(0); // 清空文件
			}
			lock.release(); // 释放文件锁
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取System.in中的输入值并写入commandQueue
	 * 
	 * @param commandQueue
	 * @param reader
	 * @return
	 * @throws InterruptedException
	 * @throws
	 */
	private static void fromSystemIn(LinkedBlockingQueue<String> commandQueue, BufferedReader reader)
			throws InterruptedException {
		try {
			if (reader.ready()) {
				String consoleCmd = reader.readLine();
				if (null != consoleCmd) {
					commandQueue.put(consoleCmd);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Thread createCommandExecuteThread(LinkedBlockingQueue<String> commandQueue) {
		return new Thread(() -> {
			while (isRunning) {
				try {
					String cmd = commandQueue.take();
					if (StringUtils.isNotBlank(cmd)) {
						Commands.execute(cmd);
					}
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					ie.printStackTrace();
				}
			}
		}, "commandExecuteThread");
	}

}