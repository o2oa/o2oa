package com.x.processplatform.core.entity.element;

import java.util.List;

import com.x.base.core.entity.SliceJpaObject;

public abstract class Activity extends SliceJpaObject {

	private static final long serialVersionUID = 4981905102396583697L;

	public abstract String getName();

	public abstract String getDescription();

	public abstract String getProcess();

	public abstract String getAlias();

	public abstract String getPosition();

	public abstract void setName(String str);

	public abstract void setDescription(String str);

	public abstract void setProcess(String str);

	public abstract void setAlias(String str);

	public abstract void setPosition(String str);

	public abstract String getForm();

	public abstract void setForm(String str);

	public abstract List<String> getReadIdentityList();

	public abstract void setReadIdentityList(List<String> readIdentityList);

	public abstract List<String> getReadDepartmentList();

	public abstract void setReadDepartmentList(List<String> readDepartmentList);

	public abstract String getReadScript();

	public abstract void setReadScript(String readScript);

	public abstract String getReadScriptText();

	public abstract void setReadScriptText(String readScriptText);

	public abstract String getReadDuty();

	public abstract List<String> getReadDataPathList();

	public abstract void setReadDataPathList(List<String> readDataPathList);

	public abstract String getReviewDuty();

	public abstract void setReviewDuty(String reviewDuty);

	public abstract List<String> getReviewDataPathList();

	public abstract void setReviewDataPathList(List<String> reviewDataPathList);

	public abstract List<String> getReviewIdentityList();

	public abstract void setReviewIdentityList(List<String> reviewIdentityList);

	public abstract List<String> getReviewDepartmentList();

	public abstract void setReviewDepartmentList(List<String> reviewDepartmentList);

	public abstract String getReviewScript();

	public abstract void setReviewScript(String reviewScript);

	public abstract String getReviewScriptText();

	public abstract void setReviewScriptText(String reviewScriptText);

	/* 是否允许调度 */
	public abstract Boolean getAllowReroute();

	public abstract void setAllowReroute(Boolean allowReroute);

	/* 是否允许调度到此节点 */
	public abstract Boolean getAllowRerouteTo();

	public abstract void setAllowRerouteTo(Boolean allowReroute);

	public ActivityType getActivityType() throws Exception {
		if (this instanceof Agent) {
			return ActivityType.agent;
		} else if (this instanceof Begin) {
			return ActivityType.begin;
		} else if (this instanceof Cancel) {
			return ActivityType.cancel;
		} else if (this instanceof Choice) {
			return ActivityType.choice;
		} else if (this instanceof Condition) {
			return ActivityType.condition;
		} else if (this instanceof Delay) {
			return ActivityType.delay;
		} else if (this instanceof Embed) {
			return ActivityType.embed;
		} else if (this instanceof End) {
			return ActivityType.end;
		} else if (this instanceof Invoke) {
			return ActivityType.invoke;
		} else if (this instanceof Manual) {
			return ActivityType.manual;
		} else if (this instanceof Merge) {
			return ActivityType.merge;
		} else if (this instanceof Message) {
			return ActivityType.message;
		} else if (this instanceof Parallel) {
			return ActivityType.parallel;
		} else if (this instanceof Service) {
			return ActivityType.service;
		} else if (this instanceof Split) {
			return ActivityType.split;
		}
		throw new Exception("invalid actvityType.");
	}

}