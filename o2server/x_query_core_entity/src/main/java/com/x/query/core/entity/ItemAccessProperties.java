package com.x.query.core.entity;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;
import java.util.Collections;
import java.util.List;

/**
 * 扩展配置
 * @author sword
 */
public class ItemAccessProperties extends JsonProperties {

	private static final long serialVersionUID = -1259157593040432239L;

	@FieldDescribe("可查看对象DN列表：人员、组织、群组、角色.")
	private List<String> readerList;

	@FieldDescribe("可查看流程活动列表.")
	private List<ItemAccessActivity> readActivityList;

	private List<String> readActivityIdList;

	@FieldDescribe("可编辑对象DN列表：人员、组织、群组、角色.")
	private List<String> editorList;

	@FieldDescribe("可编辑流程活动列表.")
	private List<ItemAccessActivity> editActivityList;

	private List<String> editActivityIdList;

	private String processId;

	public List<String> getReaderList() {
		return readerList == null ? Collections.emptyList() : readerList;
	}

	public void setReaderList(List<String> readerList) {
		this.readerList = readerList;
	}

	public List<ItemAccessActivity> getReadActivityList() {
		return readActivityList == null ? Collections.emptyList() : readActivityList;
	}

	public void setReadActivityList(
			List<ItemAccessActivity> readActivityList) {
		this.readActivityList = readActivityList;
	}

	public List<String> getReadActivityIdList() {
		return readActivityIdList == null ? Collections.emptyList() : readActivityIdList;
	}

	public void setReadActivityIdList(List<String> readActivityIdList) {
		this.readActivityIdList = readActivityIdList;
	}

	public List<String> getEditorList() {
		return editorList == null ? Collections.emptyList() : editorList;
	}

	public void setEditorList(List<String> editorList) {
		this.editorList = editorList;
	}

	public List<ItemAccessActivity> getEditActivityList() {
		return editActivityList == null ? Collections.emptyList() : editActivityList;
	}

	public void setEditActivityList(
			List<ItemAccessActivity> editActivityList) {
		this.editActivityList = editActivityList;
	}

	public List<String> getEditActivityIdList() {
		return editActivityIdList == null ? Collections.emptyList() : editActivityIdList;
	}

	public void setEditActivityIdList(List<String> editActivityIdList) {
		this.editActivityIdList = editActivityIdList;
	}
}
