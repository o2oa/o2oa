package com.x.attendance.assemble.control.jaxrs.v2;

import com.x.base.core.project.exception.PromptException;

public class ExceptionNotExistObject extends PromptException {


	private static final long serialVersionUID = 385265565348213466L;

	public ExceptionNotExistObject(String name) {
		super( "对象不存在:{}.", name);
	}
}
