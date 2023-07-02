package com.x.organization.assemble.control.jaxrs.unitattribute;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDuplicateOnUnit extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDuplicateOnUnit(String name, String unitName) {
		super("组织属性名称 {} 在同一个组织 {} 中必须唯一.", name, unitName);
	}
}
