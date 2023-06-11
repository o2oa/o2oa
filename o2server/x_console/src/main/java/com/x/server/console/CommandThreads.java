package com.x.server.console;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;

import com.x.base.core.project.config.Config;

public class CommandThreads {

	private static volatile boolean isRunning = true;

	private CommandThreads() {
		// nothing
	}

	public static void start(LinkedBlockingQueue<String> commandQueue) {
		isRunning = true;
		createConsoleCommandThread(commandQueue).start();
		createSwapCommandThread(commandQueue).start();
	}

	public static void stop() {
		isRunning = false;
	}

	private static Thread createConsoleCommandThread(LinkedBlockingQueue<String> commandQueue) {
		return new Thread(() -> {
			// 将屏幕命令输出到解析器
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
				String cmd = "";
				while (isRunning && (null != cmd)) {
					cmd = reader.readLine();
					if (null != cmd) {// 在linux环境中当前端console窗口关闭后会导致可以立即read到一个null的input值
						commandQueue.put(cmd);
						continue;
					}
					Thread.sleep(10000);
				}
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, "consoleCommandThread");
	}

	public static Thread createSwapCommandThread(LinkedBlockingQueue<String> commandQueue) {
		return new Thread(() -> {
			while (isRunning) {
				try (RandomAccessFile raf = new RandomAccessFile(Config.base() + "/command.swap", "rw");
						FileChannel channel = raf.getChannel()) {
					FileLock lock = channel.lock(); // 锁定文件
					// 读取文件内容
					long fileSize = channel.size();
					if (fileSize > 0) {
						byte[] fileBytes = new byte[(int) fileSize];
						raf.read(fileBytes);
						commandQueue.put(new String(fileBytes, StandardCharsets.UTF_8));
						// 清空文件
						channel.truncate(0);
						lock.release(); // 释放文件锁
						continue;
					}
					Thread.sleep(4000);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, "swapCommandThread");
	}
}