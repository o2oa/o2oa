package com.x.attendance.assemble.control.jaxrs.v2.appeal;

import com.x.base.core.project.exception.PromptException;

public class ExceptionPersonNotEqual extends PromptException {


	private static final long serialVersionUID = 7385829212190071815L;

	public ExceptionPersonNotEqual() {
		super(  "当前操作人和数据所属用户不符合.");
	}
}
