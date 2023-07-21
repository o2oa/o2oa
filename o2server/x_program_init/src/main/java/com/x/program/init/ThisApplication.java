package com.x.program.init;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.x.base.core.project.cache.CacheManager;

public class ThisApplication {

	private ThisApplication() {
		// nothing
	}

	private static MissionRestore missionRestore;
	private static MissionSetSecret missionSetSecret;
	private static MissionExternalDataSources missionExternalDataSources;

	protected static String path;

	public static String getPath() {
		return path;
	}

	public static MissionRestore getMissionRestore() {
		return missionRestore;
	}

	public static void setMissionRestore(MissionRestore missionRestore) {
		ThisApplication.missionRestore = missionRestore;
	}

	public static MissionSetSecret getMissionSetSecret() {
		return missionSetSecret;
	}

	public static void setMissionSetSecret(MissionSetSecret missionSetSecret) {
		ThisApplication.missionSetSecret = missionSetSecret;
	}

	public static MissionExternalDataSources getMissionExternalDataSources() {
		return missionExternalDataSources;
	}

	public static void setMissionExternalDataSources(MissionExternalDataSources missionExternalDataSources) {
		ThisApplication.missionExternalDataSources = missionExternalDataSources;
	}

	private static ExecutorService threadPool;

	public static ExecutorService threadPool() {
		return threadPool;
	}

	private static void initThreadPool() {
		int maximumPoolSize = Runtime.getRuntime().availableProcessors() + 1;
		ThreadFactory threadFactory = new ThreadFactoryBuilder()
				.setNameFormat(ThisApplication.class.getPackageName() + "-threadpool-%d").build();
		threadPool = new ThreadPoolExecutor(0, maximumPoolSize, 120, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000),
				threadFactory);
	}

	public static void init() {
		try {
			initThreadPool();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
			CacheManager.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
