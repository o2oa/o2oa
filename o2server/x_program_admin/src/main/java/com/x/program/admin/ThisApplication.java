package com.x.program.admin;

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

	private static MissionH2Upgrade missionH2Upgrade;
	private static MissionRestore missionRestore;
	private static MissionSetSecret missionSetSecret;

	public static MissionH2Upgrade getMissionH2Upgrade() {
		return missionH2Upgrade;
	}

	public static void setMissionH2Upgrade(MissionH2Upgrade missionH2Upgrade) {
		ThisApplication.missionH2Upgrade = missionH2Upgrade;
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
