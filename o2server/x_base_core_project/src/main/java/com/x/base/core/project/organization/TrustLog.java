package com.x.base.core.project.organization;

import java.util.Date;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class TrustLog extends GsonPropertyObject {

	@FieldDescribe("身份.")
	private String fromIdentity;

	@FieldDescribe("委托身份.")
	private String toIdentity;

	@FieldDescribe("应用.")
	private String application;

	@FieldDescribe("应用名称.")
	private String applicationName;

	@FieldDescribe("应用别名.")
	private String applicationAlias;

	@FieldDescribe("流程ID.")
	private String process;

	@FieldDescribe("流程名称.")
	private String processName;

	@FieldDescribe("流程别名.")
	private String processAlias;

	@FieldDescribe("当前活动ID")
	private String activity;

	@FieldDescribe("活动名称")
	private String activityName;

	@FieldDescribe("活动别名")
	private String activityAlias;

	@FieldDescribe("任务标识.")
	private String job;

	@FieldDescribe("work标识.")
	private String work;

	@FieldDescribe("委托时间.")
	private Date trustTime;

	@FieldDescribe("标题.")
	private String title;

	public TrustLog setFromIdentity(String fromIdentity) {
		this.fromIdentity = fromIdentity;
		return this;
	}

	public TrustLog setToIdentity(String toIdentity) {
		this.toIdentity = toIdentity;
		return this;
	}

	public TrustLog setApplication(String application) {
		this.application = application;
		return this;
	}

	public TrustLog setApplicationName(String applicationName) {
		this.applicationName = applicationName;
		return this;
	}

	public TrustLog setApplicationAlias(String applicationAlias) {
		this.applicationAlias = applicationAlias;
		return this;
	}

	public TrustLog setProcessName(String processName) {
		this.processName = processName;
		return this;
	}

	public TrustLog setProcessAlias(String processAlias) {
		this.processAlias = processAlias;
		return this;
	}

	public TrustLog setProcess(String process) {
		this.process = process;
		return this;
	}

	public TrustLog setTitle(String title) {
		this.title = title;
		return this;
	}

	public TrustLog setJob(String job) {
		this.job = job;
		return this;
	}

	public TrustLog setWork(String work) {
		this.work = work;
		return this;
	}

	public TrustLog setTrustTime(Date trustTime) {
		this.trustTime = trustTime;
		return this;
	}

	public TrustLog setActivity(String activity) {
		this.activity = activity;
		return this;
	}

	public TrustLog setActivityName(String activityName) {
		this.activityName = activityName;
		return this;
	}

	public TrustLog setActivityAlias(String activityAlias) {
		this.activityAlias = activityAlias;
		return this;
	}

	public String getFromIdentity() {
		return fromIdentity;
	}

	public String getToIdentity() {
		return toIdentity;
	}

	public String getApplication() {
		return application;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public String getApplicationAlias() {
		return applicationAlias;
	}

	public String getProcess() {
		return process;
	}

	public String getProcessName() {
		return processName;
	}

	public String getProcessAlias() {
		return processAlias;
	}

	public String getJob() {
		return job;
	}

	public String getWork() {
		return work;
	}

	public Date getTrustTime() {
		return trustTime;
	}

	public String getTitle() {
		return title;
	}

	public String getActivity() {
		return activity;
	}

	public String getActivityName() {
		return activityName;
	}

	public String getActivityAlias() {
		return activityAlias;
	}

}