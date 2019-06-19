package com.x.base.core.project.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.jaxrs.WrapClearCacheRequest;
import com.x.base.core.project.tools.ListTools;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@SuppressWarnings("unchecked")
public class ApplicationCache extends AbstractApplicationCache {

	private NotifyThread notifyThread;
	private ReceiveThread receiveThread;
	// private static ConcurrentHashMap<String, List<Class<?>>> incidenceMap = new
	// ConcurrentHashMap<>();

	private volatile static ApplicationCache INSTANCE;
	private CacheManager manager;

	private static Integer defaultSize = 2000;

	private static Integer defaultTimeToIdle = MINUTES_20;
	private static Integer defaultTimeToLive = MINUTES_30;

	private static String SPLIT = "#";

	public static String concreteCacheKey(Object... os) {
		return StringUtils.join(os, SPLIT);
	}

	public <T extends JpaObject> Ehcache getCache(Class<T> clz, Integer cacheSize, Integer timeToIdle,
			Integer timeToLive) {
		return this.getCache(clz.getName(), cacheSize, timeToIdle, timeToLive);
	}

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
		ClearCacheRequest req = new ClearCacheRequest();
		req.setClassName(clz.getName());
		req.setKeys(keys);
		instance().NotifyQueue.put(req);
	}

	public static <T extends JpaObject> void receive(WrapClearCacheRequest wi) throws Exception {
		instance().ReceiveQueue.put(wi);
	}

	public Ehcache getCache(String name, Integer cacheSize, Integer timeToIdle, Integer timeToLive) {
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

//	private ApplicationCache() {
//		try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo().scan()) {
//			List<ClassInfo> list = new ArrayList<>();
//			list.addAll(scanResult.getSubclasses(Deployable.class.getName()));
//			for (ClassInfo info : list) {
//				Class<?> clz = Class.forName(info.getName());
//				for (String str : clz.getAnnotation(Module.class).containerEntities()) {
//					List<Class<?>> os = incidenceMap.get(str);
//					if (null == os) {
//						os = new ArrayList<Class<?>>();
//						incidenceMap.put(str, os);
//					}
//					os.add(clz);
//				}
//			}
//			manager = createCacheManager();
//			this.notifyThread = new NotifyThread();
//			notifyThread.start();
//			this.receiveThread = new ReceiveThread();
//			receiveThread.start();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	private ApplicationCache() {
		try {
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
					WrapClearCacheRequest wi = NotifyQueue.take();
					if (wi instanceof StopNotifyThreadSignal) {
						break out;
					} else {
						String url = Config.x_program_centerUrlRoot() + "cachedispatch";
						CipherConnectionAction.put(false, url, wi);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println("ApplicationCache NotifyThread stoped!");
		}
	}

	public class ReceiveThread extends Thread {
		public void run() {
			out: while (true) {
				try {
					WrapClearCacheRequest wi = ReceiveQueue.take();
					if (wi instanceof StopReceiveThreadSignal) {
						break out;
					}
					for (String str : INSTANCE.manager.getCacheNames()) {
						/** 缓存名可能由多组组成 */
						if (ArrayUtils.contains(StringUtils.split(str, SPLIT), wi.getClassName())) {
							Ehcache cache = INSTANCE.getCache(str);
							List<Object> keys = wi.getKeys();
							if (ListTools.isNotEmpty(keys)) {
								/** 根据给定的关键字进行删除 */
								List<Object> removes = new ArrayList<>();
								for (Object key : keys) {
									for (Object o : cache.getKeys()) {
										if (Objects.equals(o, key)) {
											removes.add(o);
										}
										if (StringUtils.startsWith(o.toString(), key + SPLIT)) {
											removes.add(o);
										}
									}
								}
								if (!removes.isEmpty()) {
									cache.removeAll(removes);
								}
							} else {
								cache.removeAll();
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
		List<String> list = new ArrayList<>();
		for (Object o : objects) {
			list.add(Objects.toString(o, ""));
		}
		return StringUtils.join(list, SPLIT);
	}

	public static class ClearCacheRequest extends WrapClearCacheRequest {
	}

}