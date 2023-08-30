package com.x.processplatform.core.express.executor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;

public class ProcessPlatformKeyClassifyExecutorFactory {

	private ProcessPlatformKeyClassifyExecutorFactory() {
		// nothing
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessPlatformKeyClassifyExecutorFactory.class);

	private static final Map<String, ThreadPoolExecutor> pool = new HashMap<>();

	private static final int DISJOINT_INTERVAL = 20;

	private static int loop = 0;

	private static final ReentrantLock LOCK = new ReentrantLock();

	public static ThreadPoolExecutor get(String key) {
		LOCK.lock();
		try {
			loop = (++loop) % DISJOINT_INTERVAL;
			key = createKeyIfBlank(key);
			return (loop == 0) ? disjoint(key)
					: pool.computeIfAbsent(key, ProcessPlatformKeyClassifyExecutorFactory::createThreadPoolExecutor);
		} finally {
			LOCK.unlock();
		}
	}

	private static ThreadPoolExecutor disjoint(String key) {
		ThreadPoolExecutor executor = null;
		Iterator<Map.Entry<String, ThreadPoolExecutor>> iterator = pool.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, ThreadPoolExecutor> entry = iterator.next();
			if (StringUtils.equals(key, entry.getKey())) {
				if (!entry.getValue().isShutdown()) {
					executor = entry.getValue();
					LOGGER.debug("disjoint found existing ThreadPoolExecutor: {}.", key);
				}
			} else {
				if (entry.getValue().getQueue().isEmpty() && (entry.getValue().getActiveCount() == 0)) {
					entry.getValue().shutdown();
					iterator.remove();
					LOGGER.debug("disjoint remove ThreadPoolExecutor: {}.", key);
				}
			}
		}
		if (null == executor) {
			executor = createThreadPoolExecutor(key);
			pool.put(key, executor);
			LOGGER.debug("disjoint create ThreadPoolExecutor: {}.", key);
		}
		return executor;
	}

	private static ThreadPoolExecutor createThreadPoolExecutor(String key) {
		ThreadFactory threadFactory = new ThreadFactoryBuilder()
				.setNameFormat(ProcessPlatformKeyClassifyExecutorFactory.class.getName() + "-" + key + "-%d").build();
		return (ThreadPoolExecutor) Executors.newFixedThreadPool(1, threadFactory);
	}

	private static String createKeyIfBlank(String key) {
		if (StringUtils.isNotBlank(key)) {
			return key;
		} else {
			return StringTools.uniqueToken();
		}
	}

}
