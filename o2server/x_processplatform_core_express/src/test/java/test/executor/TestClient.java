package test.executor;

import java.util.concurrent.ThreadPoolExecutor;

import org.jblas.util.Random;

import com.x.processplatform.core.express.executor.ProcessPlatformKeyClassifyExecutorFactory;

public class TestClient {

	public static void main(String[] args) throws InterruptedException {
		ProcessPlatformKeyClassifyExecutorFactory.init(10);
		Thread thread = new Thread(() -> {
			try {
				for (int i = 0; i < 1000; i++) {
					String key = "" + Random.nextInt(50);
					RunnableImpl r = new RunnableImpl(key);
					ThreadPoolExecutor executor = ProcessPlatformKeyClassifyExecutorFactory.get(key);
					executor.submit(r);
					Thread.sleep(50);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		thread.start();
		thread.join();
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
				Thread.sleep(1000);
				System.out.println(key);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
