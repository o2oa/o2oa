package com.x.organization.assemble.control.alpha.jaxrs.person;

import com.x.base.core.exception.PromptException;

 class UniqueDuplicateException extends PromptException {

	private static final long serialVersionUID = 4433998001143598936L;

	 UniqueDuplicateException(String unqiue, String fieldName) {
		super("员工唯一标识错误:" + unqiue + ", " + fieldName + "已有值重复.");
	}
}
