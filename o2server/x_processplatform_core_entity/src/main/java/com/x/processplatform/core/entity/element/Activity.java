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

	public abstract List<String> getReadUnitList();

	public abstract void setReadUnitList(List<String> readUnitList);

	public abstract List<String> getReadGroupList();

	public abstract void setReadGroupList(List<String> readGroupList);

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

	public abstract List<String> getReviewUnitList();

	public abstract void setReviewUnitList(List<String> reviewUnitList);

	public abstract List<String> getReviewGroupList();

	public abstract void setReviewGroupList(List<String> reviewGroupList);

	public abstract String getReviewScript();

	public abstract void setReviewScript(String reviewScript);

	public abstract String getReviewScriptText();

	public abstract void setReviewScriptText(String reviewScriptText);

	public abstract String getGroup();

	public abstract void setGroup(String group);

	public abstract String getOpinionGroup();

	public abstract void setOpinionGroup(String opinionGroup);

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

	public static final String group_FIELDNAME = "group";
	public static final String opinionGroup_FIELDNAME = "opinionGroup";
	public static final String name_FIELDNAME = "name";
	public static final String alias_FIELDNAME = "alias";
	public static final String description_FIELDNAME = "description";
	public static final String process_FIELDNAME = "process";
	public static final String position_FIELDNAME = "position";
	public static final String extension_FIELDNAME = "extension";
	public static final String form_FIELDNAME = "form";
	public static final String readIdentityList_FIELDNAME = "readIdentityList";
	public static final String readUnitList_FIELDNAME = "readUnitList";
	public static final String readGroupList_FIELDNAME = "readGroupList";
	public static final String readScript_FIELDNAME = "readScript";
	public static final String readScriptText_FIELDNAME = "readScriptText";
	public static final String readDuty_FIELDNAME = "readDuty";
	public static final String readDataPathList_FIELDNAME = "readDataPathList";
	public static final String reviewIdentityList_FIELDNAME = "reviewIdentityList";
	public static final String reviewUnitList_FIELDNAME = "reviewUnitList";
	public static final String reviewGroupList_FIELDNAME = "reviewGroupList";
	public static final String reviewScript_FIELDNAME = "reviewScript";
	public static final String reviewScriptText_FIELDNAME = "reviewScriptText";
	public static final String reviewDuty_FIELDNAME = "reviewDuty";
	public static final String reviewDataPathList_FIELDNAME = "reviewDataPathList";
	public static final String beforeArriveScript_FIELDNAME = "beforeArriveScript";
	public static final String beforeArriveScriptText_FIELDNAME = "beforeArriveScriptText";
	public static final String afterArriveScript_FIELDNAME = "afterArriveScript";
	public static final String afterArriveScriptText_FIELDNAME = "afterArriveScriptText";
	public static final String beforeExecuteScript_FIELDNAME = "beforeExecuteScript";
	public static final String beforeExecuteScriptText_FIELDNAME = "beforeExecuteScriptText";
	public static final String afterExecuteScript_FIELDNAME = "afterExecuteScript";
	public static final String afterExecuteScriptText_FIELDNAME = "afterExecuteScriptText";
	public static final String beforeInquireScript_FIELDNAME = "beforeInquireScript";
	public static final String beforeInquireScriptText_FIELDNAME = "beforeInquireScriptText";
	public static final String afterInquireScript_FIELDNAME = "afterInquireScript";
	public static final String afterInquireScriptText_FIELDNAME = "afterInquireScriptText";
	public static final String allowReroute_FIELDNAME = "allowReroute";
	public static final String allowRerouteTo_FIELDNAME = "allowRerouteTo";

}