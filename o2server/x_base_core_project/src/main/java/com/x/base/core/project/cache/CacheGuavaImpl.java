package com.x.base.core.project.cache;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.WrapClearCacheRequest;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class CacheGuavaImpl implements Cache {

	private static final Logger LOGGER = LoggerFactory.getLogger(CacheGuavaImpl.class);

	private com.google.common.cache.Cache<String, Object> cache;

	private CacheGuavaNotifyReceiveQueue notifyReceiveQueue;

	private String name;

	public CacheGuavaImpl(String name) {
		this.name = name;
		try {
			cache = com.google.common.cache.CacheBuilder.newBuilder()
					.maximumSize(Config.cache().getGuava().getMaximumSize()).recordStats()
					.expireAfterWrite(Config.cache().getGuava().getExpireMinutes(), TimeUnit.MINUTES)
					.expireAfterAccess(Config.cache().getGuava().getExpireMinutes(), TimeUnit.MINUTES).build();
			this.notifyReceiveQueue = new CacheGuavaNotifyReceiveQueue(cache);
			this.notifyReceiveQueue.start();
		} catch (Exception e) {
			LOGGER.error(e);
		}
		LOGGER.debug("new CacheGuavaImpl instance:{}.", this.name);
	}

	public CacheGuavaImpl(String name, long maximumSize, long expireAfterWriteMinutes, long expireAfterAccessMinutes) {
		this.name = name;
		try {
			cache = com.google.common.cache.CacheBuilder.newBuilder().maximumSize(maximumSize).recordStats()
					.expireAfterWrite(expireAfterWriteMinutes, TimeUnit.MINUTES)
					.expireAfterAccess(expireAfterAccessMinutes, TimeUnit.MINUTES).build();
			this.notifyReceiveQueue = new CacheGuavaNotifyReceiveQueue(cache);
			this.notifyReceiveQueue.start();
		} catch (Exception e) {
			LOGGER.error(e);
		}
		LOGGER.debug("new CacheGuavaImpl instance:{}.", this.name);
	}

	@Override
	public void put(CacheCategory category, CacheKey key, Object o) {
		if ((null != category) && (null != key) && (null != o)) {
			cache.put(concrete(category, key), o);
		} else {
			if (null == category) {
				throw new IllegalStateException("cache category is null.");
			}
			if (null == key) {
				throw new IllegalStateException("cache key is null.");
			}
			throw new IllegalStateException("cache value is null.");
		}
	}

	@Override
	public Optional<Object> get(CacheCategory category, CacheKey key) {
		return Optional.ofNullable(cache.getIfPresent(concrete(category, key)));
	}

	@Override
	public void shutdown() {
		this.notifyReceiveQueue.stop();
	}

	private String concrete(CacheCategory category, CacheKey key) {
		return category.toString() + "&" + key.toString();
	}

	@Override
	public void receive(WrapClearCacheRequest wi) {
		LOGGER.debug("receive:{}.", wi::toString);
		try {
			wi.setType(WrapClearCacheRequest.TYPE_RECEIVE);
			notifyReceiveQueue.send(wi);
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	@Override
	public void notify(Class<?> clz, List<Object> keys) {
		LOGGER.debug("notify class:{}, keys:{}.", clz::toString, () -> XGsonBuilder.toJson(keys));
		try {
			ClearCacheRequest req = new ClearCacheRequest();
			req.setType(WrapClearCacheRequest.TYPE_NOTIFY);
			req.setClassName(clz.getName());
			req.setKeys(keys);
			this.notifyReceiveQueue.send(req);
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	@Override
	public String detail() {
		return this.cache.stats().toString();
	}

}
