package com.x.attendance.assemble.control.jaxrs.v2;

import com.x.base.core.project.exception.PromptException;

public class ExceptionCannotRepetitive extends PromptException {


	private static final long serialVersionUID = -5833270743616112672L;

	public ExceptionCannotRepetitive(String name) {
		super(name + "不能重复.");
	}
}
