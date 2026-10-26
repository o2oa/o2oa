package com.x.attendance.assemble.control.jaxrs.v2.file;

import com.x.base.core.project.exception.PromptException;

class ExceptionStorageMappingNotExisted extends PromptException {

	private static final long serialVersionUID = -8266649477355289556L;

	ExceptionStorageMappingNotExisted(String name) {
		super("存储器: " + name + " 不存在.");
	}
}
