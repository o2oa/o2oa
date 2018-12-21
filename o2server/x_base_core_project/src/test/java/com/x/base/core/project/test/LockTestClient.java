package com.x.base.core.project.test;

import org.junit.Test;

public class LockTestClient {

	@Test
	public void test1() throws Exception {
		Object lock = new Object();

		Thread thread1 = new Thread() {
			@Override
			public void run() {
				synchronized (lock) {
					for (int i = 0; i < 10; i++) {
						System.out.println("我是T1线程,我准备等待被其他人唤醒.");
						try {
							lock.wait();
							System.out.println("我是T1线程,我被人唤醒了.");
							Thread.sleep(1000);
							System.out.println("我是T1线程,我干了1秒钟的活,后面我就调用了notifyAll().");
							lock.notifyAll();
							System.out.println("我是T1线程,我已经调用了notifyAll().");
							Thread.sleep(1000);
							System.out.println("我是T1线程,我"+i+"次循环完成了.");							
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		Thread thread2 = new Thread() {
			@Override
			public void run() {
				synchronized (lock) {
					for (int i = 0; i < 10; i++) {
						System.out.println("我是T2线程,我准备等待被其他人唤醒.");
						try {
							lock.wait();
							System.out.println("我是T2线程,我被人唤醒了.");
							Thread.sleep(1000);
							System.out.println("我是T2线程,我干了1秒钟的活,后面我就调用了notifyAll().");
							lock.notifyAll();
							System.out.println("我是T2线程,我已经调用了notifyAll().");
							Thread.sleep(1000);
							System.out.println("我是T2线程,我"+i+"次循环完成了.");							
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		Thread thread3 = new Thread() {
			@Override
			public void run() {
				synchronized (lock) {
					System.out.println("我是T3线程,我唤醒了别人.");
					lock.notifyAll();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("唤醒后我就完成了.");
				}
			}
		};

		thread1.start();
		thread2.start();
		Thread.sleep(1000L);
		thread3.start();
		Thread.sleep(30000L);

	}

}
