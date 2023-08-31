package com.x.processplatform.core.express.executor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

	private static List<ThreadPoolExecutor> coreThreadPoolExecutors;

	public static void init(int coreSize) {
		coreThreadPoolExecutors = initCoreThreadPoolExecutors(coreSize);
	}

	public static void shutdown() {
		pool.values().stream().forEach(o -> {
			if (!o.isShutdown()) {
				o.shutdown();
			}
		});
		pool.clear();
		coreThreadPoolExecutors.stream().forEach(ThreadPoolExecutor::shutdown);
		coreThreadPoolExecutors.clear();
	}

	public static ThreadPoolExecutor get(String key) {
		LOCK.lock();
		try {
			loop = (++loop) % DISJOINT_INTERVAL;
			key = createUniqueKeyIfBlank(key);
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
				executor = entry.getValue();
				LOGGER.info("disjoint found existing ThreadPoolExecutor: {}, queue size:{}, active count;{}.", key,
						executor.getQueue().size(), executor.getActiveCount());
			} else if (idle(entry.getValue())) {
				if (!coreThreadPoolExecutors.contains(entry.getValue())) {
					entry.getValue().shutdown();
				}
				iterator.remove();
				LOGGER.info("disjoint remove ThreadPoolExecutor: {}.", entry.getKey());
			}
		}
		if (null == executor) {
			executor = createThreadPoolExecutor(key);
			pool.put(key, executor);
			LOGGER.info("disjoint create ThreadPoolExecutor: {}.", key);
		}
		return executor;
	}

	private static ThreadPoolExecutor createThreadPoolExecutor(String key) {
		Optional<ThreadPoolExecutor> opt = coreThreadPoolExecutors.stream()
				.filter(ProcessPlatformKeyClassifyExecutorFactory::idle).findFirst();
		if (opt.isPresent()) {
			return opt.get();
		}
		ThreadFactory threadFactory = new ThreadFactoryBuilder()
				.setNameFormat(ProcessPlatformKeyClassifyExecutorFactory.class.getName() + "-" + key + "-%d").build();
		return (ThreadPoolExecutor) Executors.newFixedThreadPool(1, threadFactory);
	}

	private static String createUniqueKeyIfBlank(String key) {
		if (StringUtils.isNotBlank(key)) {
			return key;
		} else {
			return StringTools.uniqueToken();
		}
	}

	private static List<ThreadPoolExecutor> initCoreThreadPoolExecutors(int coreSize) {
		return IntStream.range(0, coreSize).mapToObj(i -> {
			ThreadFactory threadFactory = new ThreadFactoryBuilder()
					.setNameFormat(ProcessPlatformKeyClassifyExecutorFactory.class.getName() + "-core" + i + "-%d")
					.build();
			return (ThreadPoolExecutor) Executors.newFixedThreadPool(1, threadFactory);
		}).collect(Collectors.toList());
	}

	private static boolean idle(ThreadPoolExecutor threadPoolExecutor) {
		return threadPoolExecutor.getQueue().isEmpty() && (threadPoolExecutor.getActiveCount() == 0);
	}

}
