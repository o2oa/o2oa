package com.x.base.core.project.queue;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
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

	private volatile boolean turn = false;

	private Integer fixedSize = 1;

	private String className = this.getClass().getName();

	private List<Object> executings = new CopyOnWriteArrayList<>();

	public boolean executing(T t) {
		if (null == t) {
			return false;
		} else {
			return executings.contains(t);
		}
	}

	public boolean contains(T t) {
		if (null == t) {
			return false;
		}
		if (this.queue.contains(t)) {
			return true;
		}
		return this.executing(t);
	}

	/**
	 * 标识自己，将会传入到执行线程中使用
	 */
	private AbstractQueue<T> abstractQueue = this;
	/**
	 * 将创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待
	 */
	private ExecutorService executorService = null;

	/**
	 * 初始化一个定长线程池
	 * 
	 * @param count
	 * @throws Exception
	 */
	public void initFixedThreadPool(Integer count) throws Exception {
		if (count == null || count < 1) {
			fixedSize = 1;
		} else {
			fixedSize = count;
		}
		logger.info(className + " new fixed thread pool with max thread count : " + count);
	}

	public void send(T t) throws Exception {
		queue.put(t);
	}

	public void start() {
		if (turn) {
			return;
		}
		turn = true;
		executorService = Executors.newFixedThreadPool(fixedSize);
		new Thread() {
			public void run() {
				Object o = null;
				while (turn) {
					try {
						o = queue.take();
						if (null != o) {
							executings.add(o);
							if (o instanceof StopSignal) {
								turn = false;
								break;
							}
							logger.debug("queue class: {} execute on message: {}.", className, gson.toJson(o));
							// 从线程池中获取空闲线程执行QueueProcessThread操作
							if (fixedSize <= 1) {
								execute((T) o);
							} else {
								executorService.execute(new QueueProcessThread(abstractQueue, o));
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							if (null != o) {
								executings.remove(o);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}.start();

		logger.info("queue class: {} start.", className);
	}

	protected abstract void execute(T t) throws Exception;

	public void stop() {
		try {
			this.queue.clear();
			queue.put(new StopSignal());
			logger.info("queue class: {} stop.", className);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (executorService != null) {
				executorService.shutdown();
			}
		}
	}

	public Boolean isEmpty() {
		return this.queue.isEmpty();
	}

	private static class StopSignal {
	}
}
