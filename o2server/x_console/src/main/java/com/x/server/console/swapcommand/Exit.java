package com.x.server.console.swapcommand;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;

public class Exit {

	public static void main(String... args) throws Exception {

		try (RandomAccessFile file = new RandomAccessFile(getBasePath() + "/command.swap", "rw");
				FileChannel fileChannel = file.getChannel()) {
			FileLock lock = fileChannel.lock();// 锁定整个文件
			file.setLength(0);
			file.write("exit".getBytes(StandardCharsets.UTF_8));
			lock.release();
		}

	}

	private static String getBasePath() {
		String path = Exit.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		File file = new File(path);
		if (!file.isDirectory()) {
			file = file.getParentFile();
		}
		while (null != file) {
			File versionFile = new File(file, "version.o2");
			if (versionFile.exists()) {
				return file.getAbsolutePath();
			}
			file = file.getParentFile();
		}
		throw new IllegalStateException("can not define o2server base directory.");
	}

}
