package com.x.base.core.project.queue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.Gson;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public abstract class AbstractQueue<T> {

	private static Logger logger = LoggerFactory.getLogger(AbstractQueue.class);

	private static Gson gson = XGsonBuilder.instance();

	private LinkedBlockingQueue<Object> queue = new LinkedBlockingQueue<>();

	private volatile boolean turn = true;

	private String className = this.getClass().getName();

	/**
	 * 标识自己，将会传入到执行线程中使用
	 */
	private AbstractQueue<T> abstractQueue = this;
	/**
	 * 将创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待
	 */
	private ExecutorService fixedThreadPool = null;

	/**
	 * 初始化一个定长线程池
	 * 
	 * @param count
	 * @throws Exception
	 */
	public void initFixedThreadPool(Integer count) throws Exception {
		if (fixedThreadPool != null) {
			throw new Exception("fixedThreadPool has init already, fixedThreadPool can not change!");
		}
		//modify by O2LEE 2017-07-28: check validity for parameter 'count' 
		if( count == null || count < 1 ) {
			count = 1;
		}
		fixedThreadPool = Executors.newFixedThreadPool( count );
		logger.info( className + " new fixed thread pool with max thread count : " + count );
	}

	public void send(T t) throws Exception {
		queue.put(t);
	}

	public void start() {
		if (fixedThreadPool == null) {
			fixedThreadPool = Executors.newFixedThreadPool(1);
		}
		new Thread() {
			public void run() {
				Object o = null;
				while (turn) {
					try {
						o = queue.take();
						if (null != o) {
							if (o instanceof StopSignal) {
								break;
							}
							logger.debug("queue class: {} execute on message: {}.", className, gson.toJson(o));
							// 从线程池中获取空闲线程执行QueueProcessThread操作
							fixedThreadPool.execute(new QueueProcessThread(abstractQueue, o));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();

		logger.info("queue class: {} start.", className);
	}

	protected abstract void execute(T t) throws Exception;

	public void stop() {
		try {
			this.turn = false;
			queue.put(new StopSignal());
			logger.info("queue class: {} stop.", className);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//modify by O2LEE 2017-07-28: add fixed thread pool shut down on queue stop 
			if( fixedThreadPool != null ) {
				fixedThreadPool.shutdown();
				fixedThreadPool = null;
			}
		}
	}

	private static class StopSignal {
	}
}
