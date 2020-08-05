package com.x.base.core.project.cache;

import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.WrapClearCacheRequest;

import redis.clients.jedis.Jedis;

public class CacheRedisNotifyThread extends Thread {

	public CacheRedisNotifyThread(Jedis jedis, LinkedBlockingQueue<WrapClearCacheRequest> queue) {
		this.jedis = jedis;
		this.queue = queue;
	}

	private Jedis jedis;

	private LinkedBlockingQueue<WrapClearCacheRequest> queue;

	@Override
	public void run() {
		while (!isInterrupted()) {
			try {
				WrapClearCacheRequest wi = queue.take();
//				String pattern = "^([\\s\\S]*)\\&(([\\s\\S]*)(" + new CacheCategory(wi.getClassName()).toString()
//						+ ")(([\\s\\S]*)\\&))($|(([\\s\\S]*)" + new CacheKey(wi.getKeys()).toString()
//						+ "([\\s\\S]*)$))";
				String match = "*&*" + new CacheCategory(wi.getClassName()).toString() + "*&*"
						+ new CacheKey(wi.getKeys()).toString() + "*";
				Set<String> keys = jedis.keys(match);
				if (!keys.isEmpty()) {
					jedis.del(keys.toArray(new String[] {}));
					jedis.flushDB();
				}
			} catch (InterruptedException ie) {
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}