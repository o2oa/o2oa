//package com.x.base.core.project.cache;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//import java.util.stream.Stream;
//
//import org.apache.commons.lang3.StringUtils;
//
//import com.x.base.core.project.cache.Cache.CacheCategory;
//import com.x.base.core.project.cache.Cache.CacheKey;
//import com.x.base.core.project.config.Config;
//import com.x.base.core.project.connection.CipherConnectionAction;
//import com.x.base.core.project.jaxrs.WrapClearCacheRequest;
//import com.x.base.core.project.logger.Logger;
//import com.x.base.core.project.logger.LoggerFactory;
//import com.x.base.core.project.queue.AbstractQueue;
//
//import net.sf.ehcache.CacheManager;
//import net.sf.ehcache.Ehcache;
//
//public class CacheGuavaNotifyReceiveQueue extends AbstractQueue<WrapClearCacheRequest> {
//
//	private static Logger logger = LoggerFactory.getLogger(CacheGuavaNotifyReceiveQueue.class);
//
//	com.google.common.cache.Cache<String, Object> cache;
//
//	public CacheGuavaNotifyReceiveQueue(com.google.common.cache.Cache<String, Object> cache) {
//		this.cache = cache;
//	}
//
//	@Override
//	protected void execute(WrapClearCacheRequest t) throws Exception {
//		if (!StringUtils.equals(t.getType(), WrapClearCacheRequest.TYPE_RECEIVE)) {
//			notify(t);
//		} else {
//			receive(t);
//		}
//	}
//
//	private void notify(WrapClearCacheRequest requst) {
//		try {
//			String url = Config.url_x_program_center_jaxrs("cachedispatch");
//			CipherConnectionAction.put(false, url, requst);
//		} catch (Exception e) {
//			logger.error(e);
//		}
//	}
//
//	private void receive(WrapClearCacheRequest request) {
//		String category = new CacheCategory(request.getClassName()).toString();
//		String keyValue = new CacheKey(request.getKeys().toArray()).toString();
//		
//		cache.asMap().keySet().forEach(k -> {
//
//		});
//		Stream.of(cacheManager.getCacheNames()).forEach(cacheName -> {
//			if (StringUtils.contains(cacheName, ) {
//				checkCache(cacheName, keyValue);
//			}
//		});
//	}
//
//	@SuppressWarnings("unchecked")
//	private void checkCache(String cacheName, String prefix) {
//		Ehcache cache = cacheManager.getCache(cacheName);
//		if (null != cache) {
//			if (StringUtils.isNotEmpty(prefix)) {
//				// 根据给定的关键字进行删除
//				List<Object> removes = new ArrayList<>();
//				cache.getKeys().forEach(o -> {
//					if (StringUtils.startsWith(Objects.toString(o, ""), prefix)) {
//						removes.add(o);
//					}
//				});
//				if (!removes.isEmpty()) {
//					cache.removeAll(removes);
//				}
//			} else {
//				cache.removeAll();
//			}
//		}
//	}
//
//}