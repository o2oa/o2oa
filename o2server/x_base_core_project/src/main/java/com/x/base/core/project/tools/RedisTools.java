package com.x.base.core.project.tools;

import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Cache.Redis;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisTools {
	private static final Logger log = LoggerFactory.getLogger(RedisTools.class);
	private static ReentrantLock lockPool = new ReentrantLock();

	private static RedisTools instance;

	private static JedisPool jedisPool;

	private static ReentrantLock lock = new ReentrantLock();

	private RedisTools() {
	}

	public static RedisTools getInstance() {
		if (instance == null) {
			lock.lock();
			instance = new RedisTools();
			lock.unlock();
		}
		return instance;
	}

	/**
	 * 初始化JedisPool
	 */
	private static void initJedisPool() throws Exception {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		// 设置最大实例总数
		jedisPoolConfig.setMaxTotal(300);
		// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
		jedisPoolConfig.setMaxIdle(5);
		jedisPoolConfig.setMinIdle(5);
		// 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
		jedisPoolConfig.setMaxWaitMillis(3 * 1000);
		// 在borrow一个jedis实例时，是否提前进行alidate操作；如果为true，则得到的jedis实例均是可用的；
		jedisPoolConfig.setTestOnBorrow(true);
		// 在还会给pool时，是否提前进行validate操作
		jedisPoolConfig.setTestOnReturn(true);
		jedisPoolConfig.setTestWhileIdle(true);
		jedisPoolConfig.setMinEvictableIdleTimeMillis(500);
		jedisPoolConfig.setSoftMinEvictableIdleTimeMillis(1000);
		jedisPoolConfig.setTimeBetweenEvictionRunsMillis(1000);
		jedisPoolConfig.setNumTestsPerEvictionRun(100);
		Redis redis = Config.cache().getRedis();
		String user = redis.getUser();
		String password = redis.getPassword();
		if (StringUtils.isBlank(redis.getUser())) {
			user = null;
		}
		if (StringUtils.isBlank(redis.getPassword())) {
			password = null;
		}
		jedisPool = new JedisPool(jedisPoolConfig, redis.getHost(), redis.getPort(), redis.getSocketTimeout(), user,
				password, redis.getIndex(), redis.getSslEnable());
	}

	/**
	 * 通用方法：从JedisPool中获取Jedis
	 *
	 * @return
	 */
	public static Jedis getJedis() {
		Jedis resource = null;
		lockPool.lock();
		try {
			if (jedisPool == null) {
				initJedisPool();
				log.debug("JedisPool init success！");
			}
			if (jedisPool != null) {
				resource = jedisPool.getResource();
			}
		} catch (Exception e) {
			log.warn("JedisPool init error: {}", e.getMessage());
		}
		lockPool.unlock();
		if (resource == null) {
			log.warn("get redis connect from redisPool is null");
		}
		return resource;
	}

	/**
	 * 通用方法：释放Jedis
	 *
	 * @param jedis
	 */
	public static void closeJedis(Jedis jedis) {
		try {
			if (jedis != null) {
				jedis.close();
			}
		} catch (Exception e) {
		}
	}

	public static void closePool() {
		try {
			if (jedisPool != null) {
				jedisPool.destroy();
			}
		} catch (Exception e) {
		}
	}

}
