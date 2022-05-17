package com.x.processplatform.core.express.service.processing.jaxrs.task;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.StringTools;

public class V2ResetWi extends GsonPropertyObject {

	private static final long serialVersionUID = -8631082471633729236L;

	@FieldDescribe("待办")
	private String task;

	@FieldDescribe("在指定待办前添加身份")
	private List<String> addBeforeList;

	@FieldDescribe("在指定待办扩充的身份")
	private List<String> extendList;

	@FieldDescribe("在指定待办后添加身份")
	private List<String> addAfterList;

	@FieldDescribe("是否删除指定待办身份")
	private Boolean remove;

	@FieldDescribe("意见")
	private String opinion;

	@FieldDescribe("操作串号")
	private String series;

	public V2ResetWi() {
		this.series = StringTools.uniqueToken();
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public List<String> getAddBeforeList() {
		return addBeforeList;
	}

	public void setAddBeforeList(List<String> addBeforeList) {
		this.addBeforeList = addBeforeList;
	}

	public List<String> getExtendList() {
		return extendList;
	}

	public void setExtendList(List<String> extendList) {
		this.extendList = extendList;
	}

	public List<String> getAddAfterList() {
		return addAfterList;
	}

	public void setAddAfterList(List<String> addAfterList) {
		this.addAfterList = addAfterList;
	}

	public Boolean getRemove() {
		return BooleanUtils.isTrue(remove);
	}

	public void setRemove(Boolean remove) {
		this.remove = remove;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

}