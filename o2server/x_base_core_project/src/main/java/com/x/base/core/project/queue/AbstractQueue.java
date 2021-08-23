package com.x.base.core.project.queue;

import java.util.concurrent.LinkedBlockingQueue;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public abstract class AbstractQueue<T> {

	private static Logger logger = LoggerFactory.getLogger(AbstractQueue.class);

	private LinkedBlockingQueue<Object> queue = new LinkedBlockingQueue<>();

	private volatile boolean turn = true;

	private String className = this.getClass().getName();

	public boolean contains(T t) {
		if (null == t) {
			return false;
		}
		return this.queue.contains(t);
	}

	public void send(T t) throws Exception {
		if (null != t) {
			queue.put(t);
		}
	}

	public void start() {
		Thread thread = new Thread(className) {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				Object o = null;
				while (turn) {
					try {
						o = queue.take();
						if (o instanceof StopSignal) {
							turn = false;
							break;
						}
						execute((T) o);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
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
		}
	}

	public Boolean isEmpty() {
		return this.queue.isEmpty();
	}

	private static class StopSignal {
		// nothing
	}
}
