package com.x.correlation.core.entity.content;

import java.util.Date;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class CorrelationProperties extends JsonProperties {

	private static final long serialVersionUID = 5628694071505848771L;

	@FieldDescribe("关联内容标题.")
	private String targetTitle;

	@FieldDescribe("关联内容分类,processPlatform:流程名称,cms:应用名称.")
	private String targetCategory;

	@FieldDescribe("关联内容创建时间")
	private Date targetStartTime;

	@FieldDescribe("关联内容创建人")
	private String targetCreatorPerson;

	@FieldDescribe("来源视图")
	private String view;

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getTargetTitle() {
		return targetTitle;
	}

	public void setTargetTitle(String targetTitle) {
		this.targetTitle = targetTitle;
	}

	public String getTargetCategory() {
		return targetCategory;
	}

	public void setTargetCategory(String targetCategory) {
		this.targetCategory = targetCategory;
	}

	public Date getTargetStartTime() {
		return targetStartTime;
	}

	public void setTargetStartTime(Date targetStartTime) {
		this.targetStartTime = targetStartTime;
	}

	public String getTargetCreatorPerson() {
		return targetCreatorPerson;
	}

	public void setTargetCreatorPerson(String targetCreatorPerson) {
		this.targetCreatorPerson = targetCreatorPerson;
	}

}