//package com.x.processplatform.service.processing;
//
//import java.util.Objects;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.zip.CRC32;
//
//import com.x.base.core.project.config.Config;
//
//public class ExecutorServiceFactory {
//
//	private static ExecutorService[] executors;
//
//	private static int count;
//
//	public static ExecutorService get(String seed) throws Exception {
//		if (null == executors) {
//			synchronized (ExecutorServiceFactory.class) {
//				if (null == executors) {
//					count = Config.processPlatform().getExecutorCount();
//					executors = new ExecutorService[count];
//					for (int i = 0; i < count; i++) {
//						executors[i] = Executors.newSingleThreadExecutor();
//					}
//				}
//			}
//		}
//		CRC32 crc32 = new CRC32();
//		crc32.update(Objects.toString(seed, "").getBytes());
//		int idx = (int) (crc32.getValue() % count);
//		return executors[idx];
//	}
//
//}
