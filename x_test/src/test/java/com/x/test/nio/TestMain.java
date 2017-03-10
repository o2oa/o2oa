package com.x.test.nio;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.FileLock;

public class TestMain {

	private static RandomAccessFile raf;

	public static void main(String... args) {
		try {
			// 建立文件和内存的映射，即时双向同步
			raf = new RandomAccessFile("E:/swap.nio", "rw");
			FileChannel fc = raf.getChannel();
			MappedByteBuffer mbb = fc.map(MapMode.READ_WRITE, 0, 1024);
			FileLock flock = null;
			// 阻塞方法一：非阻塞独占锁，当文件锁不可用时，tryLock()会得到null值
			// do {
			// flock=fc.tryLock();
			// } while(null == flock);
			// 阻塞方法二：非阻塞共享锁，当文件锁不可用时，tryLock()会得到null值
			// fc.tryLock(0L, Long.MAX_VALUE, true);
			// 阻塞方法三：阻塞共享锁，有写操作会报异常
			// flock = fc.lock(0L, Long.MAX_VALUE, true);
			while (true) {
				flock = fc.lock();
				byte b = mbb.get(0);
				System.out.println((int) b);
				mbb.put(0, (byte) 0);
				flock.release();// 释放锁
				Thread.sleep(2000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
