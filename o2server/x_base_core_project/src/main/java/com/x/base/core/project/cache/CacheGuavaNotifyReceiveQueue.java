package com.x.base.core.project.cache;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.jaxrs.WrapClearCacheRequest;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;

public class CacheGuavaNotifyReceiveQueue extends AbstractQueue<WrapClearCacheRequest> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CacheGuavaNotifyReceiveQueue.class);

	com.google.common.cache.Cache<String, Object> cache;

	public CacheGuavaNotifyReceiveQueue(com.google.common.cache.Cache<String, Object> cache) {
		this.cache = cache;
	}

	@Override
	protected void execute(WrapClearCacheRequest t) throws Exception {
		if (!StringUtils.equals(t.getType(), WrapClearCacheRequest.TYPE_RECEIVE)) {
			notify(t);
		} else {
			receive(t);
		}
	}

	private void notify(WrapClearCacheRequest requst) {
		try {
			CipherConnectionAction.put(false, Config.url_x_program_center_jaxrs("cachedispatch"), requst);
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void receive(WrapClearCacheRequest request) {
		CacheCategory cacheCategory = new CacheCategory(request.getClassName());
		List<String> invalidateKeys = new ArrayList<>();
		cache.asMap().keySet().forEach(k -> {
			if (containsCacheCategory(k, cacheCategory)) {
				if (request.getKeys().isEmpty()) {
					invalidateKeys.add(k);
				} else {
					CacheKey cacheKey = new CacheKey(request.getKeys().toArray());
					if (containsCacheKey(k, cacheKey)) {
						invalidateKeys.add(k);
					}
				}
			}
		});
		if (!invalidateKeys.isEmpty()) {
			cache.invalidateAll(invalidateKeys);
		}
	}

	private boolean containsCacheCategory(String k, CacheCategory cacheCategory) {
		return StringUtils.contains(StringUtils.substringBefore(k, "&"), cacheCategory.toString());
	}

	private boolean containsCacheKey(String k, CacheKey cacheKey) {
		return StringUtils.contains(StringUtils.substringAfter(k, "&"), cacheKey.toString());
	}

}