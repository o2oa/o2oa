package com.x.base.core.project.cache;

import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.jaxrs.WrapClearCacheRequest;
import com.x.base.core.project.tools.RedisTools;

import redis.clients.jedis.Jedis;

public class CacheRedisNotifyThread extends Thread {

	public CacheRedisNotifyThread(LinkedBlockingQueue<WrapClearCacheRequest> queue) {
		this.setName("CacheManager-" + CacheRedisNotifyThread.class.getSimpleName());
		this.queue = queue;
	}

	private LinkedBlockingQueue<WrapClearCacheRequest> queue;

	@Override
	public void run() {
		while (!isInterrupted()) {
			try {
				WrapClearCacheRequest wi = queue.take();
				String match = "*&*" + new CacheCategory(wi.getClassName()).toString() + "*&*"
						+ new CacheKey(wi.getKeys()).toString() + "*";
				Jedis jedis = RedisTools.getJedis();
				if (jedis != null) {
					Set<String> keys = jedis.keys(match);
					if (!keys.isEmpty()) {
						jedis.del(keys.toArray(new String[] {}));
						jedis.flushDB();
					}
					RedisTools.closeJedis(jedis);
				}
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}