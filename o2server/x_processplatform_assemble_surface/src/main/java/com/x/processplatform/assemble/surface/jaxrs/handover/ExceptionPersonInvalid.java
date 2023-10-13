package com.x.processplatform.assemble.surface.jaxrs.handover;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonInvalid extends PromptException {

	private static final long serialVersionUID = 8077614120025290009L;

	ExceptionPersonInvalid() {
		super("用户需为全称,如：张三@zhangsan@P.");
	}
}
