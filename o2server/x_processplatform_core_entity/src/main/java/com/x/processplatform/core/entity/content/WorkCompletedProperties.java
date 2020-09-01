package com.x.processplatform.core.entity.content;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Script;

public class WorkCompletedProperties extends JsonProperties {

	@FieldDescribe("合并记录对象")
	private List<Record> recordList = new ArrayList<>();

	@FieldDescribe("合并数据对象")
	private Data data;

	@FieldDescribe("合并工作日志对象")
	private List<WorkLog> workLogList = new ArrayList<>();

	@FieldDescribe("合并工作Form")
	private Form form;

	@FieldDescribe("合并工作relatedFormList")
	private List<Form> relatedFormList = new ArrayList<>();

	@FieldDescribe("合并工作relatedScriptList")
	private List<Script> relatedScriptList = new ArrayList<>();

	@FieldDescribe("标题")
	private String title;

	public List<Record> getRecordList() {
		return recordList;
	}

	public void setRecordList(List<Record> recordList) {
		this.recordList = recordList;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public List<WorkLog> getWorkLogList() {
		return workLogList;
	}

	public void setWorkLogList(List<WorkLog> workLogList) {
		this.workLogList = workLogList;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public List<Form> getRelatedFormList() {
		return relatedFormList;
	}

	public List<Script> getRelatedScriptList() {
		return relatedScriptList;
	}

	public void setRelatedFormList(List<Form> relatedFormList) {
		this.relatedFormList = relatedFormList;
	}

	public void setRelatedScriptList(List<Script> relatedScriptList) {
		this.relatedScriptList = relatedScriptList;
	}

}
