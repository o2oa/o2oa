package test.executor;

import java.util.concurrent.ThreadPoolExecutor;

import org.jblas.util.Random;

import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

public class TestClient {

	public static void main(String[] args) throws InterruptedException {
		ProcessPlatformKeyClassifyExecutorFactory.init(10);
		var thread1 = new SupplierThread();
		var thread2 = new SupplierThread();
		var thread3 = new SupplierThread();
		var thread4 = new SupplierThread();
		thread1.start();
		thread2.start();
		thread3.start();
		thread4.start();
		thread1.join();
		thread2.join();
		thread3.join();
		thread4.join();
		ProcessPlatformKeyClassifyExecutorFactory.shutdown();
	}

	public static class RunnableImpl implements Runnable {

		private String key;

		public RunnableImpl(String key) {
			this.key = key;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(200);
				System.out.println(key);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static class SupplierThread extends Thread {
		@Override
		public void run() {
			try {
				for (int i = 0; i < 1000; i++) {
					String key = "" + Random.nextInt(99);
					RunnableImpl r = new RunnableImpl(key);
					ThreadPoolExecutor executor = ProcessPlatformKeyClassifyExecutorFactory.get(key);
					executor.submit(r);
					Thread.sleep(19);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}