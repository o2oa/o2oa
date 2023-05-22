package com.x.attendance.assemble.control.jaxrs.v2.mobile;

import com.x.base.core.project.exception.PromptException;

public class ExceptionTimeError extends PromptException {


	private static final long serialVersionUID = 717644218197145320L;

	public ExceptionTimeError(String message) {
		super(  message + ".");
	}
}
