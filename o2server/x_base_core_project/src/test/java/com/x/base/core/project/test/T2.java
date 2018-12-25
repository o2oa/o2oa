package com.x.base.core.project.test;

public class T2 implements Runnable {

	private Object lock;

	public T2(Object object) {
		this.lock = object;
	}

	@Override
	public void run() {
		synchronized (lock) {
			for (int i = 0; i < 10; i++) {
				System.out.println("我是T2线程,我准备等待被其他人唤醒.");
				try {
					lock.notifyAll();
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("我是T2线程,我被人唤醒了.");
			}
		}
	}
}
