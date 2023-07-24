package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionChangeNameDenied extends PromptException {

	private static final long serialVersionUID = 9085364457175859374L;

	ExceptionChangeNameDenied(String name) {
		super("不符合规则的名称：{}.", name);
	}

}
