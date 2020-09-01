package com.x.base.core.project.cache;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.cache.ApplicationCache.ClearCacheRequest;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.jaxrs.WrapClearCacheRequest;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.management.ManagementService;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

public class CacheEhcacheImpl implements Cache {

	private CacheManager cacheManager;

	private LinkedBlockingQueue<WrapClearCacheRequest> notifyQueue;

	private LinkedBlockingQueue<WrapClearCacheRequest> receiveQueue;

	private CacheEhcacheReceiveThread receiveThread;

	private CacheEhcacheNotifyThread notifyThread;

	public CacheEhcacheImpl(String application) throws Exception {
		CacheConfiguration cacheConfiguration = new CacheConfiguration();
		cacheConfiguration.maxEntriesLocalHeap(1000);
		cacheConfiguration.persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.NONE));
		cacheConfiguration.timeToIdleSeconds(1800);
		cacheConfiguration.timeToLiveSeconds(3600);
		cacheConfiguration.memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU);
		Configuration configuration = new Configuration();
		configuration.setDefaultCacheConfiguration(cacheConfiguration);
		configuration.setName(application);
		this.cacheManager = new CacheManager(configuration);
		if (BooleanUtils.isTrue(Config.cache().getEhcache().getJmxEnable())) {
			ManagementService.registerMBeans(cacheManager, ManagementFactory.getPlatformMBeanServer(), true, true, true,
					true);
		}
		this.receiveQueue = new LinkedBlockingQueue<>();
		this.receiveThread = new CacheEhcacheReceiveThread(cacheManager, receiveQueue);
		this.receiveThread.start();
		this.notifyQueue = new LinkedBlockingQueue<>();
		this.notifyThread = new CacheEhcacheNotifyThread(notifyQueue);
		this.notifyThread.start();
	}

	@Override
	public Optional<Object> get(CacheCategory category, CacheKey key) throws Exception {
		Element element = this.getCache(category.toString()).get(key.toString());
		if (element != null) {
			return Optional.ofNullable(element.getObjectValue());
		} else {
			return Optional.empty();
		}
	}

	@Override
	public void put(CacheCategory category, CacheKey key, Object o) throws Exception {
		if (null != o) {
			this.getCache(category.toString()).put(new Element(key.toString(), o));
		}
	}

	@Override
	public void receive(WrapClearCacheRequest wi) throws Exception {
		receiveQueue.put(wi);
	}

	@Override
	public void notify(Class<?> clz, List<Object> keys) throws Exception {
		ClearCacheRequest req = new ClearCacheRequest();
		req.setClassName(clz.getName());
		req.setKeys(keys);
		this.notifyQueue.put(req);
	}

	@Override
	public void shutdown() {
		this.receiveThread.interrupt();
		this.notifyThread.interrupt();
		this.cacheManager.shutdown();
	}

	private synchronized Ehcache getCache(String name) {
		return cacheManager.addCacheIfAbsent(name);
	}

}
