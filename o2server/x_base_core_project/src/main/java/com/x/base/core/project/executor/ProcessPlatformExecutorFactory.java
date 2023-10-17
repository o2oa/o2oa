//package com.x.base.core.project.executor;
//
//import java.util.Objects;
//import java.util.concurrent.ExecutorService;
//import java.util.zip.CRC32;
//
//import com.x.base.core.project.config.Config;
//
//public class ProcessPlatformExecutorFactory {
//
//	private ProcessPlatformExecutorFactory() {
//		throw new IllegalStateException("ProcessPlatformExecutorFactory class");
//	}
//
//	private static ExecutorService[] executors;
//
//	public static synchronized ExecutorService get(final String seed) throws Exception {
//		if (null == executors) {
//			executors = Config.resource_node_processPlatformExecutors();
//		}
//		CRC32 crc32 = new CRC32();
//		crc32.update(Objects.toString(seed, "").getBytes());
//		int idx = (int) (Math.abs(crc32.getValue()) % executors.length);
//		return executors[idx];
//	}
//
//}