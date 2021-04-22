package com.x.organization.assemble.control.jaxrs.unit;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDuplicateName extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDuplicateName(String name) {
		super("组织 {} 在同一层级下必须唯一.", name);
	}
}
