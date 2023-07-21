package com.x.organization.assemble.control.jaxrs.unitattribute;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionUnitAttributeNotExist extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionUnitAttributeNotExist(String flag) {
		super("组织属性:{}, 不存在.", flag);
	}
}
