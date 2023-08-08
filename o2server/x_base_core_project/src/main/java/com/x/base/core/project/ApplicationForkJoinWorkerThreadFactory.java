package com.x.base.core.project;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.ForkJoinWorkerThread;

public class ApplicationForkJoinWorkerThreadFactory implements ForkJoinWorkerThreadFactory {
	private final String threadNamePrefix;

	public ApplicationForkJoinWorkerThreadFactory(Package p) {
		this.threadNamePrefix = p.getName();
	}

	@Override
	public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
		return new ApplicationForkJoinWorkerThread(pool);
	}

	private class ApplicationForkJoinWorkerThread extends ForkJoinWorkerThread {
		protected ApplicationForkJoinWorkerThread(ForkJoinPool pool) {
			super(pool);
			this.setName(threadNamePrefix + "-poolIndex-" + this.getPoolIndex() + "-id-" + this.getId());
		}
	}
}