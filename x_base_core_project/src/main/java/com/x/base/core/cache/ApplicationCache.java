package com.x.base.core.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.x.base.core.Packages;
import com.x.base.core.application.Application;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.Assemble;
import com.x.base.core.project.Service;
import com.x.base.core.utils.ListTools;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@SuppressWarnings("unchecked")
public class ApplicationCache extends AbstractApplicationCache {

	private NotifyThread notifyThread;
	private ReceiveThread receiveThread;
	private static ConcurrentHashMap<String, List<Class<?>>> incidenceMap = new ConcurrentHashMap<>();

	private volatile static ApplicationCache INSTANCE;
	private CacheManager manager;

	private static Integer defaultSize = 2000;

	private static Integer defaultTimeToIdle = MINUTES_20;
	private static Integer defaultTimeToLive = MINUTES_30;

	private static String SPLIT = ",";

	public <T extends JpaObject> Ehcache getCache(Class<T> clz) {
		return this.getCache(clz.getName(), defaultSize, defaultTimeToIdle, defaultTimeToLive);
	}

	public Ehcache getCache(Class<?>... classes) {
		List<String> classNames = new ArrayList<>();
		for (Class<?> clz : classes) {
			classNames.add(clz.getName());
		}
		return this.getCache(StringUtils.join(classNames, SPLIT), defaultSize, defaultTimeToIdle, defaultTimeToLive);
	}

	private Ehcache getCache(String name) {
		return this.getCache(name, defaultSize, defaultTimeToIdle, defaultTimeToLive);
	}

	public <T extends JpaObject> Element get(Class<T> clz, String key) {
		return this.getCache(clz).get(key);
	}

	public <T extends JpaObject> void put(Class<T> clz, String key, Object value) {
		this.getCache(clz).put(new Element(key, value));
	}

	public void put(String key, Object value, Class<? extends JpaObject>... classes) {
		this.getCache(classes).put(new Element(key, value));
	}

	public static <T extends JpaObject> void notify(Class<T> clz) throws Exception {
		List<Object> list = new ArrayList<>();
		notify(clz, list);
	}

	public static <T extends JpaObject> void notify(Class<T> clz, Object... objects) throws Exception {
		List<Object> list = new ArrayList<>();
		for (Object o : objects) {
			list.add(o);
		}
		notify(clz, list);
	}

	public static <T extends JpaObject> void notify(Class<T> clz, List<Object> keys) throws Exception {
		ClearCacheRequest clearCacheRequest = new ClearCacheRequest();
		clearCacheRequest.setClassName(clz.getName());
		clearCacheRequest.setKeys(keys);
		instance().NotifyQueue.put(clearCacheRequest);
	}

	public static <T extends JpaObject> void receive(ClearCacheRequest clearCacheRequest) throws Exception {
		instance().ReceiveQueue.put(clearCacheRequest);
	}

	private Ehcache getCache(String name, Integer cacheSize, Integer timeToIdle, Integer timeToLive) {
		Ehcache cache = manager.getCache(name);
		if (null != cache) {
			return cache;
		} else {
			synchronized (ApplicationCache.class) {
				cache = manager.getCache(name);
				if (null == cache) {
					cache = new Cache(cacheConfiguration(name, cacheSize, timeToIdle, timeToLive));
					manager.addCache(cache);
				}
			}
		}
		return cache;
	}

	private ApplicationCache() {
		try {
			List<String> classes = new ArrayList<>();
			ScanResult scanResult = new FastClasspathScanner(Packages.PREFIX).scan();
			classes.addAll(scanResult.getNamesOfSubclassesOf(Assemble.class));
			classes.addAll(scanResult.getNamesOfSubclassesOf(Service.class));
			for (String o : classes) {
				Class<?> clz = Class.forName(o);
				for (String str : (List<String>) FieldUtils.readStaticField(clz, "containerEntities")) {
					List<Class<?>> list = incidenceMap.get(str);
					if (null == list) {
						list = new ArrayList<Class<?>>();
						incidenceMap.put(str, list);
					}
					list.add(clz);
				}
			}
			manager = createCacheManager();
			this.notifyThread = new NotifyThread();
			notifyThread.start();
			this.receiveThread = new ReceiveThread();
			receiveThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ApplicationCache instance() {
		if (INSTANCE == null) {
			synchronized (ApplicationCache.class) {
				if (INSTANCE == null) {
					INSTANCE = new ApplicationCache();
				}
			}
		}
		return INSTANCE;
	}

	public static void shutdown() {
		if (INSTANCE != null) {
			synchronized (ApplicationCache.class) {
				try {
					INSTANCE.ReceiveQueue.put(new StopReceiveThreadSignal());
					INSTANCE.NotifyQueue.put(new StopNotifyThreadSignal());
					INSTANCE.manager.shutdown();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void clear() {
		if (INSTANCE != null) {
			synchronized (ApplicationCache.class) {
				INSTANCE.manager.clearAll();
			}
		}
	}

	public class NotifyThread extends Thread {
		public void run() {
			out: while (true) {
				try {
					ClearCacheRequest clearCacheRequest = NotifyQueue.take();
					if (clearCacheRequest instanceof StopNotifyThreadSignal) {
						break out;
					} else {
						dispatch(clearCacheRequest);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println("ApplicationCache NotifyThread stoped!");
		}
	}

	private void dispatch(ClearCacheRequest clearCacheRequest) {
		for (Class<?> cls : ListTools.nullToEmpty(incidenceMap.get(clearCacheRequest.getClassName()))) {
			for (Application o : ListTools.nullToEmpty(AbstractThisApplication.applications.get(cls.getName()))) {
				try {
					AbstractThisApplication.applications.putQuery(o, "cache", clearCacheRequest);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public class ReceiveThread extends Thread {
		public void run() {
			out: while (true) {
				try {
					ClearCacheRequest clearCacheRequest = ReceiveQueue.take();
					if (clearCacheRequest instanceof StopReceiveThreadSignal) {
						break out;
					} else {
						for (String str : INSTANCE.manager.getCacheNames()) {
							if (StringUtils.equalsIgnoreCase(str, clearCacheRequest.getClassName())) {
								Ehcache cache = INSTANCE.getCache(str);
								List<Object> keys = clearCacheRequest.getKeys();
								if (!ListTools.isEmpty(clearCacheRequest.getKeys())) {
									cache.removeAll(keys);
								} else {
									cache.removeAll();
								}
							}

						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println("ApplicationCache ReceiveThread stoped!");
		}
	}

	public String generateKey(Object... objects) {
		return XGsonBuilder.pureGsonDateFormated().toJson(objects);
	}

}