package com.x.organization.assemble.control.jaxrs.unit;

import com.x.base.core.project.exception.PromptException;

class ExceptionNameInvalid extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionNameInvalid(String name) {
		super("组织 {} 名称不符合要求.", name);
	}
}
