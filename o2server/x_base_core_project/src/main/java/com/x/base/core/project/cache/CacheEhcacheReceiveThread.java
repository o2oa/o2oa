package com.x.base.core.project.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.jaxrs.WrapClearCacheRequest;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

public class CacheEhcacheReceiveThread extends Thread {

	public CacheEhcacheReceiveThread(CacheManager cacheManager, LinkedBlockingQueue<WrapClearCacheRequest> queue) {
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
				String keyValue = new CacheKey(wi.getKeys().toArray()).toString();
				Stream.of(cacheManager.getCacheNames()).forEach(cacheName -> {
					if (StringUtils.contains(cacheName, new CacheCategory(wi.getClassName()).toString())) {
						checkCache(cacheName, keyValue);
					}
				});
			} catch (InterruptedException e) {
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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