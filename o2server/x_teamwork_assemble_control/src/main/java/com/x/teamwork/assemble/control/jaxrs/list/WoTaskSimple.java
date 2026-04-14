package com.x.teamwork.assemble.control.jaxrs.list;

import java.util.Date;

import com.x.base.core.project.annotation.FieldDescribe;

public class WoTaskSimple {
	
	@FieldDescribe("数据库主键,自动生成.")
	private String id ;
	
	@FieldDescribe("所属项目ID.")
	private String project;

	@FieldDescribe("所属项目名称.")
	private String projectName;

	@FieldDescribe("父级工作任务ID.")
	private String parent;	

	@FieldDescribe("工作任务名称（40字）")
	private String name;

	@FieldDescribe("工作任务概括（80字）")
	private String summay;

	@FieldDescribe("工作开始时间")
	private Date startTime;

	@FieldDescribe("工作开始时间")
	private Date endTime;

	@FieldDescribe("工作等级：普通 | 紧急 | 特急")
	private String priority = "普通";

	@FieldDescribe("工作状态：执行中- processing | 已完成- completed | 已归档- archived")
	private String workStatus = "processing";

	@FieldDescribe("是否已完成")
	private Boolean completed = false;

	@FieldDescribe("是否已认领")
	private Boolean claimed = false;

	@FieldDescribe("是否已超时")
	private Boolean overtime = false;

	@FieldDescribe("是否已经归档")
	private Boolean archive = false;

	@FieldDescribe("工作进度：记录4位数，显示的时候除以100")
	private Integer progress = 0;

	@FieldDescribe("执行者和负责人")
	private String executor;	

	@FieldDescribe("创建者，可能为System，如果由系统创建。")
	private String creatorPerson;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSummay() {
		return summay;
	}

	public void setSummay(String summay) {
		this.summay = summay;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(String workStatus) {
		this.workStatus = workStatus;
	}

	public Boolean getCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	public Boolean getClaimed() {
		return claimed;
	}

	public void setClaimed(Boolean claimed) {
		this.claimed = claimed;
	}

	public Boolean getOvertime() {
		return overtime;
	}

	public void setOvertime(Boolean overtime) {
		this.overtime = overtime;
	}

	public Boolean getArchive() {
		return archive;
	}

	public void setArchive(Boolean archive) {
		this.archive = archive;
	}

	public Integer getProgress() {
		return progress;
	}

	public void setProgress(Integer progress) {
		this.progress = progress;
	}

	public String getExecutor() {
		return executor;
	}

	public void setExecutor(String executor) {
		this.executor = executor;
	}

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
	}
}