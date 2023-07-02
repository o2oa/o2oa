package com.x.portal.assemble.surface;

import com.x.base.core.project.gson.GsonPropertyObject;

public class Control extends GsonPropertyObject {
	/* 是否可以看到 */
	private Boolean allowVisit;
	/* 是否可以直接流转 */
	private Boolean allowProcessing;
	/* 是否可以处理待阅 */
	private Boolean allowReadProcessing;
	/* 是否可以保存数据 */
	private Boolean allowSave;
	/* 是否可以重置处理人 */
	private Boolean allowReset;
	/* 是否可以待阅处理人 */
	private Boolean allowReadReset;
	/* 是否可以召回 */
	private Boolean allowRetract;
	/* 是否可以调度 */
	private Boolean allowReroute;
	/* 是否可以删除 */
	private Boolean allowDelete;

	public Boolean getAllowSave() {
		return allowSave;
	}

	public void setAllowSave(Boolean allowSave) {
		this.allowSave = allowSave;
	}

	public Boolean getAllowReset() {
		return allowReset;
	}

	public void setAllowReset(Boolean allowReset) {
		this.allowReset = allowReset;
	}

	public Boolean getAllowRetract() {
		return allowRetract;
	}

	public void setAllowRetract(Boolean allowRetract) {
		this.allowRetract = allowRetract;
	}

	public Boolean getAllowReroute() {
		return allowReroute;
	}

	public void setAllowReroute(Boolean allowReroute) {
		this.allowReroute = allowReroute;
	}

	public Boolean getAllowProcessing() {
		return allowProcessing;
	}

	public void setAllowProcessing(Boolean allowProcessing) {
		this.allowProcessing = allowProcessing;
	}

	public Boolean getAllowDelete() {
		return allowDelete;
	}

	public void setAllowDelete(Boolean allowDelete) {
		this.allowDelete = allowDelete;
	}

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

	public Boolean getAllowReadReset() {
		return allowReadReset;
	}

	public void setAllowReadReset(Boolean allowReadReset) {
		this.allowReadReset = allowReadReset;
	}

}