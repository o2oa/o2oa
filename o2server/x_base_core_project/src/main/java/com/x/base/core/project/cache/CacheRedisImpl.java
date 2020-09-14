package com.x.base.core.project.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Cache.Redis;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.jaxrs.WrapClearCacheRequest;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

public class CacheRedisImpl implements Cache {

	private LinkedBlockingQueue<WrapClearCacheRequest> notifyQueue;

	private Jedis jedis;

	private String application;

	private SetParams setParams;

	private CacheRedisNotifyThread notifyThread;

	public CacheRedisImpl(String application) throws Exception {
		Redis redis = Config.cache().getRedis();
		this.jedis = new Jedis(redis.getHost(), redis.getPort(), redis.getConnectionTimeout(), redis.getSocketTimeout(),
				redis.getSslEnable());
		if (StringUtils.isNotBlank(redis.getUser()) && StringUtils.isNotBlank(redis.getPassword())) {
			this.jedis.auth(redis.getUser(), redis.getPassword());
		} else if (StringUtils.isNotBlank(redis.getPassword())) {
			this.jedis.auth(redis.getPassword());
		}
		jedis.select(redis.getIndex());
		this.notifyQueue = new LinkedBlockingQueue<>();
		this.application = application;
		this.setParams = new SetParams();
		this.setParams.px(1000 * 60 * 60);
		this.notifyThread = new CacheRedisNotifyThread(jedis, notifyQueue);
		this.notifyThread.start();
	}

	@Override
	public void put(CacheCategory category, CacheKey key, Object o) throws Exception {
		if (null != o) {
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(baos)) {
				oos.writeObject(o);
				byte[] bytes = baos.toByteArray();
				jedis.set(concrete(category, key).getBytes(StandardCharsets.UTF_8), bytes, setParams);
			}
		}
	}

	@Override
	public Optional<Object> get(CacheCategory category, CacheKey key) throws Exception {
		byte[] bytes = jedis.get(concrete(category, key).getBytes(StandardCharsets.UTF_8));
		if ((null != bytes) && bytes.length > 0) {
			try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
					ObjectInputStream ois = new ObjectInputStream(bais)) {
				return Optional.ofNullable(ois.readObject());
			}
		} else {
			return Optional.empty();
		}
	}

	@Override
	public void shutdown() {
		this.notifyThread.interrupt();
		this.jedis.close();
	}

	@Override
	public void receive(WrapClearCacheRequest wi) throws Exception {
		// nothing
	}

	@Override
	public void notify(Class<?> clz, List<Object> keys) throws Exception {
		Wi wi = new Wi();
		wi.setClassName(clz.getName());
		wi.setKeys(keys);
		this.notifyQueue.put(wi);
	}

	private String concrete(CacheCategory category, CacheKey key) {
		return this.application + "&" + category.toString() + "&" + key.toString();
	}

	public static class Wi extends WrapClearCacheRequest {

	}

}
