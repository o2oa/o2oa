package com.x.test.nio;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.FileLock;

import org.junit.Test;

public class TestClient {

	private static RandomAccessFile raf;

	public static void main(String... args) {
		try {
			// 建立文件和内存的映射，即时双向同步
			raf = new RandomAccessFile("E:/swap.nio", "rw");
			FileChannel fc = raf.getChannel();
			MappedByteBuffer mbb = fc.map(MapMode.READ_WRITE, 0, 1024);
			FileLock flock = null;
			flock = fc.lock();
			mbb.put(0, (byte) 1);
			flock.release();
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public static void test1() {
		//ActionResult<WrapOutBoolean> result = new 
	}
}
