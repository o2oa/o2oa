package com.x.processplatform.assemble.designer;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class CompareApplication extends GsonPropertyObject {

	@FieldDescribe("导入名称")
	private String name;

	@FieldDescribe("导入id")
	private String id;

	@FieldDescribe("导入别名")
	private String alias;

	@FieldDescribe("是否已经存在")
	private Boolean exist;

	@FieldDescribe("已经存在名称")
	private String existName;

	@FieldDescribe("已经存在id")
	private String existId;

	@FieldDescribe("已经存在别名")
	private String existAlias;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Boolean getExist() {
		return exist;
	}

	public void setExist(Boolean exist) {
		this.exist = exist;
	}

	public String getExistName() {
		return existName;
	}

	public void setExistName(String existName) {
		this.existName = existName;
	}

	public String getExistId() {
		return existId;
	}

	public void setExistId(String existId) {
		this.existId = existId;
	}

	public String getExistAlias() {
		return existAlias;
	}

	public void setExistAlias(String existAlias) {
		this.existAlias = existAlias;
	}

}
