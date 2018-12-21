package com.x.organization.assemble.control.jaxrs.personattribute;

import com.x.base.core.project.exception.PromptException;

class ExceptionDuplicateWithName extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDuplicateWithName(String name) {
		super("个人属性名称 : {} 与现有的标识冲突.", name);
	}
}
