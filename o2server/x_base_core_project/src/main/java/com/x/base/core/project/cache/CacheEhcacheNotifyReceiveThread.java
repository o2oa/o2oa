package com.x.base.core.project.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.jaxrs.WrapClearCacheRequest;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

public class CacheEhcacheNotifyReceiveThread extends Thread {

	private static Logger logger = LoggerFactory.getLogger(CacheEhcacheNotifyReceiveThread.class);

	public CacheEhcacheNotifyReceiveThread(CacheManager cacheManager,
			LinkedBlockingQueue<WrapClearCacheRequest> queue) {
		this.cacheManager = cacheManager;
		this.queue = queue;
	}

	private CacheManager cacheManager;
	private LinkedBlockingQueue<WrapClearCacheRequest> queue;

	@Override
	public void run() {
		while (!isInterrupted()) {
			try {
				WrapClearCacheRequest wi = queue.take();
				if (!StringUtils.equals(wi.getType(), WrapClearCacheRequest.TYPE_RECEIVE)) {
					notify(wi);
				} else {
					receive(wi);
				}
			} catch (Exception e) {
				logger.error(e);
				Thread.currentThread().interrupt();
			}
		}
	}

	private void notify(WrapClearCacheRequest requst) {
		try {
			String url = Config.url_x_program_center_jaxrs("cachedispatch");
			CipherConnectionAction.put(false, url, requst);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void receive(WrapClearCacheRequest request) {
		String keyValue = new CacheKey(request.getKeys().toArray()).toString();
		Stream.of(cacheManager.getCacheNames()).forEach(cacheName -> {
			if (StringUtils.contains(cacheName, new CacheCategory(request.getClassName()).toString())) {
				checkCache(cacheName, keyValue);
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void checkCache(String cacheName, String prefix) {
		Ehcache cache = cacheManager.getCache(cacheName);
		if (null != cache) {
			if (StringUtils.isNotEmpty(prefix)) {
				// 根据给定的关键字进行删除
				List<Object> removes = new ArrayList<>();
				cache.getKeys().forEach(o -> {
					if (StringUtils.startsWith(Objects.toString(o, ""), prefix)) {
						removes.add(o);
					}
				});
				if (!removes.isEmpty()) {
					cache.removeAll(removes);
				}
			} else {
				cache.removeAll();
			}
		}
	}

}