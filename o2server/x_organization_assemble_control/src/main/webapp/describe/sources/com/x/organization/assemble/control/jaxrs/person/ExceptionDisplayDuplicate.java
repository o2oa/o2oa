package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.PromptException;

 class ExceptionDisplayDuplicate extends PromptException {

	private static final long serialVersionUID = 4433998001143598936L;

	 ExceptionDisplayDuplicate(String display, String fieldName) {
		super("显示名错误:" + display + ", " + fieldName + "已有值重复.");
	}
}
