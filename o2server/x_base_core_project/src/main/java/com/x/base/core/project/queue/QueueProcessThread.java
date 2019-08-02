package com.x.base.core.project.queue;

public class QueueProcessThread<T> implements Runnable {

	private T o = null;

	private AbstractQueue<T> queue = null;

	public QueueProcessThread(AbstractQueue<T> queue, T o) {
		this.queue = queue;
		this.o = o;
	}

	@Override
	public void run() {
		try {
			queue.execute(o);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
