package com.x.teamwork.core.entity.tools;

import com.x.base.core.project.annotation.FieldDescribe;

public class FieldInfo {

	@FieldDescribe("扩展属性名")
	private String fieldName = null;
	
	@FieldDescribe("扩展属性类别")
	private String fieldType = null;
	
	@FieldDescribe("扩展属性最大长度")
	private Integer maxLength = null;
	
	public FieldInfo( String fieldName, String fieldType, Integer maxLength ) {
		super();
		this.fieldName = fieldName;
		this.fieldType = fieldType;
		this.maxLength = maxLength;
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

	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}	
}