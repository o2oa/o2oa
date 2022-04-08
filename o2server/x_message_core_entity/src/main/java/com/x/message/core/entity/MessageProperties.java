package com.x.message.core.entity;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class MessageProperties extends JsonProperties {

	private static final long serialVersionUID = -4943657103264120657L;

	@FieldDescribe("失败次数")
	private Integer failure;
	@FieldDescribe("错误信息")
	private String error;

	public Integer getFailure() {
		return failure;
	}

	public void setFailure(Integer failure) {
		this.failure = failure;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
