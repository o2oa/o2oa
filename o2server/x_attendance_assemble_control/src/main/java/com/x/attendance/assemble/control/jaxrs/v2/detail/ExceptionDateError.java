package com.x.attendance.assemble.control.jaxrs.v2.detail;

import com.x.base.core.project.exception.PromptException;

public class ExceptionDateError extends PromptException {


	private static final long serialVersionUID = 5464536178018732958L;

	public ExceptionDateError() {
		super(  "日期不正确，请输入一个过去的日期，格式：yyyy-MM-dd.");
	}
}
