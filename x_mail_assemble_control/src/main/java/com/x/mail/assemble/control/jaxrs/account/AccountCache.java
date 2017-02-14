package com.x.mail.assemble.control.jaxrs.account;

import java.util.List;

import com.google.gson.Gson;
import com.x.base.core.application.Application;
import com.x.base.core.entity.JpaObject;
import com.x.mail.assemble.control.AbstractAccountCache;
import com.x.mail.assemble.control.ThisApplication;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class AccountCache extends AbstractAccountCache {

	private volatile static AccountCache INSTANCE;
	private CacheManager manager;

	private static Integer defaultSize = 2000;

	private static Integer defaultTimeToIdle = MINUTES_20;
	private static Integer defaultTimeToLive = MINUTES_20;

	public Ehcache getCache(Class<? extends JpaObject> clz) {
		return this.getCache(clz.getCanonicalName(), defaultSize, defaultTimeToIdle, defaultTimeToLive);
	}

	public Ehcache getCache(String name) {
		return this.getCache(name, defaultSize, defaultTimeToIdle, defaultTimeToLive);
	}

	public <T extends JpaObject> Element get(Class<T> clz, String key) {
		return this.getCache(clz).get(key);
	}

	public <T extends JpaObject> void put(Class<T> clz, String key, Object value) {
		this.getCache(clz).put(new Element(key, value));
	}

	public <T extends JpaObject> void notifyClear(Class<T> clz) throws Exception {
		NotifyQueue.put(clz);
	}

	public Ehcache getCache(Class<?> clz, String method, Integer cacheSize, Integer timeToIdle, Integer timeToLive) {
		String name = clz.getCanonicalName() + "." + method;
		return this.getCache(name, cacheSize, timeToIdle, timeToLive);
	}

	public Ehcache getCache(String name, Integer cacheSize, Integer timeToIdle, Integer timeToLive) {
		Ehcache cache = manager.getCache(name);
		if (null != cache) {
			return cache;
		} else {
			synchronized (AccountCache.class) {
				cache = manager.getCache(name);
				if (null == cache) {
					cache = new Cache(cacheConfiguration(name, cacheSize, timeToIdle, timeToLive));
					manager.addCache(cache);
				}
			}
		}
		return cache;
	}

	public boolean clearCache(String name) {
		if (manager.cacheExists(name)) {
			Ehcache cache = manager.getCache(name);
			cache.removeAll();
			return true;
		} else {
			return false;
		}
	}

	private AccountCache() {
		manager = createCacheManager();
		NotifyThread thread = new NotifyThread();
		thread.start();
	}

	public static AccountCache instance() {
		if (INSTANCE == null) {
			synchronized (AccountCache.class) {
				if (INSTANCE == null) {
					INSTANCE = new AccountCache();
				}
			}
		}
		return INSTANCE;
	}

	public static void shutdown() {
		if (INSTANCE != null) {
			synchronized (AccountCache.class) {
				INSTANCE.manager.shutdown();
			}
		}
	}

	public static void clear() {
		if (INSTANCE != null) {
			synchronized (AccountCache.class) {
				INSTANCE.manager.clearAll();
			}
		}
	}

	public class NotifyThread extends Thread {
		public void run() {
			while (true) {
				try {
					Class<? extends JpaObject> clz = NotifyQueue.take();
					for (List<Application> list : ThisApplication.applications.values()) {
						for (Application o : list) {
							ThisApplication.applications.deleteQuery(o, "cache", clz);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void clear(Class<? extends JpaObject> clz) {
		if (INSTANCE != null) {
			synchronized (AccountCache.class) {
				INSTANCE.getCache(clz).removeAll();
			}
		}
	}

	public String generateKey(Object... objects) {
		return (new Gson()).toJson(objects);
	}

}