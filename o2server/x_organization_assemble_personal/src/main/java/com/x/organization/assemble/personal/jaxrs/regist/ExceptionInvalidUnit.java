package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidUnit extends PromptException {

	private static final long serialVersionUID = 6647381353782082070L;

	ExceptionInvalidUnit(String unit) {
		super("指定的组织不存在:{}.", unit);
	}
}
