package com.x.base.core.project;

public abstract class AbstractContext {
	/** Applications资源 */
	protected volatile Applications applications;

	public Applications applications() {
		synchronized (this) {
			return this.applications;
		}
	}

}
