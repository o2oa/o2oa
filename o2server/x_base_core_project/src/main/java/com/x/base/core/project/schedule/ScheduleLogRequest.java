package com.x.base.core.project.schedule;

import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.StringTools;

public class ScheduleLogRequest extends GsonPropertyObject {

	private static final long serialVersionUID = -1472780383077011677L;

	public static final String FIELDSCHEDULELOGID = "scheduleLogId";

	// 新增id字段,用于保存ScheduleLog的时候覆盖自动生成的id
	private String scheduleLogId;

	private String type;

	private String node;

	private String application;

	private String className;

	private Date fireTime;

	private Long elapsed;

	private String stackTrace;

	private Boolean success;

	public ScheduleLogRequest(JobExecutionContext jobExecutionContext) {
		JobDetail jobDetail = jobExecutionContext.getJobDetail();
		this.scheduleLogId = StringTools.uniqueToken();
		jobExecutionContext.put(FIELDSCHEDULELOGID, scheduleLogId);
		this.className = jobDetail.getKey().getName();
		this.application = jobDetail.getKey().getGroup();
		this.node = jobDetail.getDescription();
		this.type = jobExecutionContext.getTrigger().getDescription();
		this.fireTime = jobExecutionContext.getFireTime();
	}

	public ScheduleLogRequest(JobExecutionContext jobExecutionContext, JobExecutionException jobExecutionException) {
		JobDetail jobDetail = jobExecutionContext.getJobDetail();
		this.scheduleLogId = Objects.toString(jobExecutionContext.get(FIELDSCHEDULELOGID));
		this.className = jobDetail.getKey().getName();
		this.application = jobDetail.getKey().getGroup();
		this.node = jobDetail.getDescription();
		this.type = jobExecutionContext.getTrigger().getDescription();
		this.elapsed = jobExecutionContext.getJobRunTime();
		this.fireTime = jobExecutionContext.getFireTime();
		if (null != jobExecutionException) {
			this.stackTrace = ExceptionUtils.getStackTrace(jobExecutionException);
			this.success = false;
		} else {
			this.success = true;
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Date getFireTime() {
		return fireTime;
	}

	public void setFireTime(Date fireTime) {
		this.fireTime = fireTime;
	}

	public Long getElapsed() {
		return elapsed;
	}

	public void setElapsed(Long elapsed) {
		this.elapsed = elapsed;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getScheduleLogId() {
		return scheduleLogId;
	}

	public void setScheduleLogId(String scheduleLogId) {
		this.scheduleLogId = scheduleLogId;
	}

}