package com.x.base.core.project.cache;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.x.base.core.project.jaxrs.WrapClearCacheRequest;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class CacheGuavaImpl implements Cache {

	private static final Logger LOGGER = LoggerFactory.getLogger(CacheGuavaImpl.class);

	private com.google.common.cache.Cache<String, Object> cache;

	//private CacheGuavaNotifyReceiveQueue notifyReceiveQueue;

	private String application;

	public CacheGuavaImpl(String application) {
		this.application = application;
		cache = com.google.common.cache.CacheBuilder.newBuilder().maximumSize(1000)
				.expireAfterAccess(30L, TimeUnit.MINUTES).build();
	}

	@Override
	public void put(CacheCategory category, CacheKey key, Object o) throws Exception {
		cache.put(concrete(category, key), o);

	}

	@Override
	public Optional<?> get(CacheCategory category, CacheKey key) throws Exception {
		return Optional.ofNullable(cache.getIfPresent(concrete(category, key)));
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
	}

	@Override
	public void receive(WrapClearCacheRequest wi) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void notify(Class<?> clz, List<Object> keys) throws Exception {
//		ClearCacheRequest req = new ClearCacheRequest();
//		req.setType(WrapClearCacheRequest.TYPE_NOTIFY);
//		req.setClassName(clz.getName());
//		req.setKeys(keys);
//		this.notifyReceiveQueue.send(req);

	}

	private String concrete(CacheCategory category, CacheKey key) {
		return this.application + "&" + category.toString() + "&" + key.toString();
	}

}
