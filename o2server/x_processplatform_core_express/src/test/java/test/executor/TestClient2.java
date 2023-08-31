package test.executor;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class TestClient2 {

	public static void main(String[] args) {

		Date start = new Date();
		for (int i = 0; i < 1000; i++) {
			ThreadPoolExecutor o = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
			o.submit(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					//System.out.println(1);
					return null;
				}
			});
			o.shutdown();
		}
		Date stop = new Date();
		System.out.println("use:" + (stop.getTime() - start.getTime()));

	}

}
