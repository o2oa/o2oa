package com.x.base.core.project.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

import com.x.base.core.project.tools.RedisTools;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Cache.Redis;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.jaxrs.WrapClearCacheRequest;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

public class CacheRedisImpl implements Cache {

	private LinkedBlockingQueue<WrapClearCacheRequest> notifyQueue;

	private String application;

	private SetParams setParams;

	private CacheRedisNotifyThread notifyThread;

	public CacheRedisImpl(String application) throws Exception {
		this.notifyQueue = new LinkedBlockingQueue<>();
		this.application = application;
		this.setParams = new SetParams();
		this.setParams.px(1000 * 60 * 60);
		this.notifyThread = new CacheRedisNotifyThread(notifyQueue);
		this.notifyThread.start();
	}

	@Override
	public void put(CacheCategory category, CacheKey key, Object o) throws Exception {

		if (null != o) {
			Jedis jedis = RedisTools.getJedis();
			if(jedis != null) {
				try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
					 ObjectOutputStream oos = new ObjectOutputStream(baos)) {
					oos.writeObject(o);
					byte[] bytes = baos.toByteArray();
					jedis.set(concrete(category, key).getBytes(StandardCharsets.UTF_8), bytes, setParams);
				}
				RedisTools.closeJedis(jedis);
			}
		}

	}

	@Override
	public Optional<Object> get(CacheCategory category, CacheKey key) throws Exception {
		Jedis jedis = RedisTools.getJedis();
		if(jedis != null) {
			byte[] bytes = jedis.get(concrete(category, key).getBytes(StandardCharsets.UTF_8));
			RedisTools.closeJedis(jedis);
			if ((null != bytes) && bytes.length > 0) {
				try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
					 ObjectInputStream ois = new ObjectInputStream(bais)) {
					return Optional.ofNullable(ois.readObject());
				}
			}
		}
		return Optional.empty();
	}

	@Override
	public void shutdown() {
		this.notifyThread.interrupt();
		RedisTools.closePool();
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

	/*private Jedis getJedis() throws Exception{
		Redis redis = Config.cache().getRedis();
		Jedis jedis = new Jedis(redis.getHost(), redis.getPort(), redis.getConnectionTimeout(), redis.getSocketTimeout(), redis.getSslEnable());
		if (StringUtils.isNotBlank(redis.getUser()) && StringUtils.isNotBlank(redis.getPassword())) {
			jedis.auth(redis.getUser(), redis.getPassword());
		} else if (StringUtils.isNotBlank(redis.getPassword())) {
			jedis.auth(redis.getPassword());
		}
		jedis.select(redis.getIndex());
		return jedis;
	}*/

	private String concrete(CacheCategory category, CacheKey key) {
		return this.application + "&" + category.toString() + "&" + key.toString();
	}

	public static class Wi extends WrapClearCacheRequest {

	}

}
