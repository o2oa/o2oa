package com.x.query.core.entity;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

	@FieldDescribe("可编辑对象DN列表：人员、组织、群组、角色.")
	private List<String> editorList;

	@FieldDescribe("可编辑流程活动列表.")
	private List<ItemAccessActivity> editActivityList;

	@FieldDescribe("扩展信息.")
	private String extension;

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
		return getReadActivityList().stream().map(ItemAccessActivity::getUnique).distinct().collect(
				Collectors.toList());
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

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}
}
