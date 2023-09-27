package com.x.processplatform.assemble.surface;

import com.x.base.core.project.gson.GsonPropertyObject;

public class Control extends GsonPropertyObject {

	private static final long serialVersionUID = -4033469981088793137L;

	// 是否可以管理
	private Boolean allowManage;
	// 是否可以看到
	private Boolean allowVisit;
	// 是否可以直接流转
	private Boolean allowProcessing;
	// 是否可以处理待阅
	private Boolean allowReadProcessing;
	// 是否可以保存数据
	private Boolean allowSave;
	// 是否可以重置处理人
	private Boolean allowReset;
	// 是否可以加签
	private Boolean allowAddTask;
	// 是否可以调度
	private Boolean allowReroute;
	// 是否可以删除
	private Boolean allowDelete;
	// 是否可以增加会签分支
	private Boolean allowAddSplit;
	// 是否可以召回
	private Boolean allowRetract;
	// 是否可以回滚
	private Boolean allowRollback;
	// 是否可以提醒
	private Boolean allowPress;
	// 是否可以待办挂起(暂停待办计时)
	private Boolean allowPause;
	// 是否可以取消待办挂起(恢复待办计时)
	private Boolean allowResume;
	// 是否可以退回
	private Boolean allowGoBack;
	// 是否可以终止
	private Boolean allowTerminate;

	/**
	 * 权限必须要查的，下面两个字段不用二次查询 工作标题
	 */
	private String workTitle;
	// 工作的job
	private String workJob;

	public Boolean getAllowManage() {
		return allowManage;
	}

	public void setAllowManage(Boolean allowManage) {
		this.allowManage = allowManage;
	}

	public Boolean getAllowVisit() {
		return allowVisit;
	}

	public void setAllowVisit(Boolean allowVisit) {
		this.allowVisit = allowVisit;
	}

	public Boolean getAllowProcessing() {
		return allowProcessing;
	}

	public void setAllowProcessing(Boolean allowProcessing) {
		this.allowProcessing = allowProcessing;
	}

	public Boolean getAllowReadProcessing() {
		return allowReadProcessing;
	}

	public void setAllowReadProcessing(Boolean allowReadProcessing) {
		this.allowReadProcessing = allowReadProcessing;
	}

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

	public Boolean getAllowAddTask() {
		return allowAddTask;
	}

	public void setAllowAddTask(Boolean allowAddTask) {
		this.allowAddTask = allowAddTask;
	}

	public Boolean getAllowReroute() {
		return allowReroute;
	}

	public void setAllowReroute(Boolean allowReroute) {
		this.allowReroute = allowReroute;
	}

	public Boolean getAllowDelete() {
		return allowDelete;
	}

	public void setAllowDelete(Boolean allowDelete) {
		this.allowDelete = allowDelete;
	}

	public Boolean getAllowAddSplit() {
		return allowAddSplit;
	}

	public void setAllowAddSplit(Boolean allowAddSplit) {
		this.allowAddSplit = allowAddSplit;
	}

	public Boolean getAllowRetract() {
		return allowRetract;
	}

	public void setAllowRetract(Boolean allowRetract) {
		this.allowRetract = allowRetract;
	}

	public Boolean getAllowRollback() {
		return allowRollback;
	}

	public void setAllowRollback(Boolean allowRollback) {
		this.allowRollback = allowRollback;
	}

	public Boolean getAllowPress() {
		return allowPress;
	}

	public void setAllowPress(Boolean allowPress) {
		this.allowPress = allowPress;
	}

	public Boolean getAllowPause() {
		return allowPause;
	}

	public void setAllowPause(Boolean allowPause) {
		this.allowPause = allowPause;
	}

	public Boolean getAllowResume() {
		return allowResume;
	}

	public void setAllowResume(Boolean allowResume) {
		this.allowResume = allowResume;
	}

	public Boolean getAllowGoBack() {
		return allowGoBack;
	}

	public void setAllowGoBack(Boolean allowGoBack) {
		this.allowGoBack = allowGoBack;
	}

	public Boolean getAllowTerminate() {
		return allowTerminate;
	}

	public void setAllowTerminate(Boolean allowTerminate) {
		this.allowTerminate = allowTerminate;
	}

	public String getWorkTitle() {
		return workTitle;
	}

	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}

	public String getWorkJob() {
		return workJob;
	}

	public void setWorkJob(String workJob) {
		this.workJob = workJob;
	}
}
