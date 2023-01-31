package com.x.attendance.assemble.control.jaxrs.v2.shift;

import com.x.base.core.project.exception.PromptException;

class ExceptionNotExistObject extends PromptException {


	private static final long serialVersionUID = 385265565348213466L;

	ExceptionNotExistObject(String name) {
		super(name + "不存在！");
	}
}
