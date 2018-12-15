package com.x.base.core.project.test;

import org.junit.Test;

public class LockTestClient2 {

	@Test
	public void test1() throws Exception {
		Object lock = new Object();

		Thread thread1 = new Thread(new T1(lock));
		Thread thread2 = new Thread(new T2(lock));

		thread1.start();
		Thread.sleep(3000L);
		thread2.start();
		Thread.sleep(30000L);

	}

}
