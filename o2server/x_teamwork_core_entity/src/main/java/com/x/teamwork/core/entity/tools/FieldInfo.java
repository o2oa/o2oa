package com.x.teamwork.core.entity.tools;

import com.x.base.core.project.annotation.FieldDescribe;

public class FieldInfo {

	@FieldDescribe("扩展属性名")
	private String fieldName = null;
	
	@FieldDescribe("扩展属性类别")
	private String fieldType = null;
	
	public FieldInfo( String fieldName, String fieldType ) {
		super();
		this.fieldName = fieldName;
		this.fieldType = fieldType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
}