//package com.x.base.core.project.cache;
//
//import java.lang.management.ManagementFactory;
//import java.util.List;
//import java.util.Optional;
//
//import org.apache.commons.lang3.BooleanUtils;
//
//import com.x.base.core.project.config.Config;
//import com.x.base.core.project.jaxrs.WrapClearCacheRequest;
//import com.x.base.core.project.logger.Logger;
//import com.x.base.core.project.logger.LoggerFactory;
//
//import net.sf.ehcache.CacheManager;
//import net.sf.ehcache.Ehcache;
//import net.sf.ehcache.Element;
//import net.sf.ehcache.config.CacheConfiguration;
//import net.sf.ehcache.config.Configuration;
//import net.sf.ehcache.config.PersistenceConfiguration;
//import net.sf.ehcache.management.ManagementService;
//import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
//
//public class CacheEhcacheImpl implements Cache {
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(CacheEhcacheImpl.class);
//
//	private CacheManager cacheManager;
//
//	private CacheEhcacheNotifyReceiveQueue notifyReceiveQueue;
//
//	public CacheEhcacheImpl(String application) throws Exception {
//		CacheConfiguration cacheConfiguration = new CacheConfiguration();
//		cacheConfiguration.maxEntriesLocalHeap(1000);
//		cacheConfiguration.persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.NONE));
//		cacheConfiguration.timeToIdleSeconds(1800);
//		cacheConfiguration.timeToLiveSeconds(3600);
//		cacheConfiguration.memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU);
//		Configuration configuration = new Configuration();
//		configuration.setDefaultCacheConfiguration(cacheConfiguration);
//		configuration.setName("CacheEhcacheManager-" + application);
//		this.cacheManager = new CacheManager(configuration);
//		this.notifyReceiveQueue = new CacheEhcacheNotifyReceiveQueue(this.cacheManager);
//		notifyReceiveQueue.start();
//		if (BooleanUtils.isTrue(Config.cache().getEhcache().getJmxEnable())) {
//			ManagementService.registerMBeans(cacheManager, ManagementFactory.getPlatformMBeanServer(), true, true, true,
//					true);
//		}
//	}
//
//	@Override
//	public Optional<Object> get(CacheCategory category, CacheKey key) {
//		Element element = this.getCache(category.toString()).get(key.toString());
//		if (element != null) {
//			return Optional.ofNullable(element.getObjectValue());
//		} else {
//			return Optional.empty();
//		}
//	}
//
//	@Override
//	public void put(CacheCategory category, CacheKey key, Object o) {
//		if (null != o) {
//			this.getCache(category.toString()).put(new Element(key.toString(), o));
//		}
//	}
//
//	@Override
//	public void receive(WrapClearCacheRequest wi) {
//		try {
//			wi.setType(WrapClearCacheRequest.TYPE_RECEIVE);
//			notifyReceiveQueue.send(wi);
//		} catch (Exception e) {
//			LOGGER.error(e);
//		}
//	}
//
//	@Override
//	public void notify(Class<?> clz, List<Object> keys) {
//		try {
//			ClearCacheRequest req = new ClearCacheRequest();
//			req.setType(WrapClearCacheRequest.TYPE_NOTIFY);
//			req.setClassName(clz.getName());
//			req.setKeys(keys);
//			this.notifyReceiveQueue.send(req);
//		} catch (Exception e) {
//			LOGGER.error(e);
//		}
//	}
//
//	@Override
//	public void shutdown() {
//		this.notifyReceiveQueue.stop();
//		this.cacheManager.shutdown();
//	}
//
//	@Override
//	public String detail() {
//		return cacheManager.getActiveConfigurationText();
//	}
//
//	private synchronized Ehcache getCache(String name) {
//		return cacheManager.addCacheIfAbsent(name);
//	}
//
//}
