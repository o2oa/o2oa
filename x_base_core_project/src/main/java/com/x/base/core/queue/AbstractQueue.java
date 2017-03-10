package com.x.base.core.queue;

import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.Gson;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

public abstract class AbstractQueue<T> {

	private static Logger logger = LoggerFactory.getLogger(AbstractQueue.class);

	private static Gson gson = XGsonBuilder.instance();

	private LinkedBlockingQueue<Object> queue = new LinkedBlockingQueue<>();

	private volatile boolean turn = true;

	private String className = this.getClass().getName();

	public void send(T t) throws Exception {
		queue.put(t);
	}

	public void start() {
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
							execute((T) o);
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
		}
	}

	private static class StopSignal {
	}
}