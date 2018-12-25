package com.x.processplatform.assemble.surface;

import com.x.base.core.project.gson.GsonPropertyObject;

public abstract class WorkCompletedControl extends GsonPropertyObject {
	/* 是否可以看到 */
	private Boolean allowVisit;
	/* 是否可以处理待阅 */
	private Boolean allowReadProcessing;
	/* 是否可以删除 */
	private Boolean allowDelete;

	public Boolean getAllowVisit() {
		return allowVisit;
	}

	public void setAllowVisit(Boolean allowVisit) {
		this.allowVisit = allowVisit;
	}

	public Boolean getAllowReadProcessing() {
		return allowReadProcessing;
	}

	public void setAllowReadProcessing(Boolean allowReadProcessing) {
		this.allowReadProcessing = allowReadProcessing;
	}

	public Boolean getAllowDelete() {
		return allowDelete;
	}

	public void setAllowDelete(Boolean allowDelete) {
		this.allowDelete = allowDelete;
	}

	/** 是否可以在管理界面直接尝试流转文件 */

}