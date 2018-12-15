package com.x.base.core.project.cache;

import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import com.x.base.core.project.jaxrs.WrapClearCacheRequest;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

public abstract class AbstractApplicationCache {

	public static Integer MINUTES_1 = 60 * 1;
	public static Integer MINUTES_2 = 60 * 2;
	public static Integer MINUTES_5 = 60 * 5;
	public static Integer MINUTES_10 = 60 * 10;
	public static Integer MINUTES_20 = 60 * 20;
	public static Integer MINUTES_30 = 60 * 30;
	public static Integer MINUTES_60 = 60 * 60;
	public static Integer MINUTES_120 = 60 * 120;

	protected LinkedBlockingQueue<WrapClearCacheRequest> NotifyQueue = new LinkedBlockingQueue<>();

	protected LinkedBlockingQueue<WrapClearCacheRequest> ReceiveQueue = new LinkedBlockingQueue<>();

	protected static CacheConfiguration cacheConfiguration(String name, int size, int timeToIdle, int timeToLive) {
		CacheConfiguration cacheConfiguration = new CacheConfiguration(name, size);
		cacheConfiguration.persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.NONE));
		cacheConfiguration.timeToIdleSeconds(timeToIdle);
		cacheConfiguration.timeToLiveSeconds(timeToLive);
		cacheConfiguration.memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU);
		return cacheConfiguration;
	}

	protected static CacheConfiguration cacheConfiguration(int size, int timeToIdle, int timeToLive) {
		return cacheConfiguration(UUID.randomUUID().toString(), size, timeToIdle, timeToLive);
	}

	protected static CacheManager createCacheManager() {
		Configuration configuration = new Configuration();
		configuration.setDefaultCacheConfiguration(cacheConfiguration("default", 10, 100000, 100000));
		return CacheManager.create(configuration);
	}

	public static class StopReceiveThreadSignal extends WrapClearCacheRequest {

	}

	public static class StopNotifyThreadSignal extends WrapClearCacheRequest {

	}

}
