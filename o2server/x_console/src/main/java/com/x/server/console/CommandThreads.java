package com.x.server.console;

import java.io.Console;
import java.io.IOError;
import java.io.IOException;
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

	private static volatile boolean running = true;

	private static Thread commandFromConsoleThread;
	private static Thread commandFromFileThread;
	private static Thread commandExecuteThread;

	public static void join() {
		try {
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
		running = true;
		commandFromConsoleThread = createCommandFromConsoleThread(commandQueue);
		commandFromFileThread = createCommandFromFileThread(commandQueue);
		commandExecuteThread = createCommandExecuteThread(commandQueue);
		commandFromConsoleThread.start();
		commandFromFileThread.start();
		commandExecuteThread.start();
	}

	public static void stop() {
		running = false;
		if (null != commandFromConsoleThread && commandFromConsoleThread.isAlive()) {
			commandFromConsoleThread.interrupt();
		}
		if (null != commandFromFileThread) {
			commandFromFileThread.interrupt();
		}
		if (null != commandExecuteThread) {
			commandExecuteThread.interrupt();
		}
	}

	private static Thread createCommandFromConsoleThread(LinkedBlockingQueue<String> commandQueue) {
		return new Thread(() -> {
			Console console;
			while (running && ((console = System.console()) != null)) {
				try {
					fromConsole(commandQueue, console);
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (IOError ioe) {
					break;
				}
			}
		}, "commandFromConsoleThread");
	}

	private static Thread createCommandFromFileThread(LinkedBlockingQueue<String> commandQueue) {
		return new Thread(() -> {
			while (running) {
				try {
					fromFile(commandQueue);
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}, "commandFromFileThread");
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
	 * 从System.console读取输入值并写入commandQueue,需要使用System.console如果使用system.in导致windows控制台没有输出,可以盲打.
	 * 
	 * @param commandQueue
	 * @param reader
	 * @return
	 * @throws InterruptedException
	 * @throws
	 */
	private static void fromConsole(LinkedBlockingQueue<String> commandQueue, Console console)
			throws InterruptedException {
		if (null != console) {
			String consoleCmd = console.readLine();
			if (null != consoleCmd) {
				commandQueue.put(consoleCmd);
			}
		}
	}

	private static Thread createCommandExecuteThread(LinkedBlockingQueue<String> commandQueue) {
		return new Thread(() -> {
			while (running) {
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