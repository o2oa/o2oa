package com.x.base.core.project.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

import org.graalvm.polyglot.Source;

import com.x.base.core.project.jaxrs.WrapClearCacheRequest;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.RedisTools;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

public class CacheRedisImpl implements Cache {

	private static final Logger LOGGER = LoggerFactory.getLogger(CacheRedisImpl.class);

	private LinkedBlockingQueue<WrapClearCacheRequest> notifyQueue;

	private String application;

	private SetParams setParams;

	private CacheRedisNotifyThread notifyThread;

	public CacheRedisImpl(String application) {
		this.notifyQueue = new LinkedBlockingQueue<>();
		this.application = application;
		this.setParams = new SetParams();
		this.setParams.px(3600000);
		this.notifyThread = new CacheRedisNotifyThread(notifyQueue);
		this.notifyThread.start();
	}

	@Override
	public void put(CacheCategory category, CacheKey key, Object o) {
		// 无法序列化CompiledScript类型,在使用Redis缓存无法缓存CompiledScript类型,直接跳过
		if ((null != o) && (!(o instanceof Source))) {
			try (Jedis jedis = RedisTools.getJedis();
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(baos)) {
				oos.writeObject(o);
				byte[] bytes = baos.toByteArray();
				jedis.set(concrete(category, key).getBytes(StandardCharsets.UTF_8), bytes, setParams);
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}
	}

	@Override
	public Optional<Object> get(CacheCategory category, CacheKey key) {
		try (Jedis jedis = RedisTools.getJedis()) {
			byte[] bytes = jedis.get(concrete(category, key).getBytes(StandardCharsets.UTF_8));
			if ((null != bytes) && bytes.length > 0) {
				try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
						ObjectInputStream ois = new ObjectInputStream(bais)) {
					return Optional.ofNullable(ois.readObject());
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return Optional.empty();
	}

	@Override
	public void shutdown() {
		this.notifyThread.interrupt();
		RedisTools.closePool();
	}

	@Override
	public void receive(WrapClearCacheRequest wi) {
		// nothing
	}

	@Override
	public void notify(Class<?> clz, List<Object> keys) {
		try {
			Wi wi = new Wi();
			wi.setClassName(clz.getName());
			wi.setKeys(keys);
			this.notifyQueue.put(wi);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			LOGGER.error(e);
		}
	}

	@Override
	public String detail() {
		return "";
	}

	private String concrete(CacheCategory category, CacheKey key) {
		return this.application + "&" + category.toString() + "&" + key.toString();
	}

	public static class Wi extends WrapClearCacheRequest {
		private static final long serialVersionUID = 9200759731216457906L;
	}

}
