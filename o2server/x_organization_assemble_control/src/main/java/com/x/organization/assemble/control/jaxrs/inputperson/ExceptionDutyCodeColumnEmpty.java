package com.x.organization.assemble.control.jaxrs.inputperson;

import com.x.base.core.project.exception.PromptException;

class ExceptionDutyCodeColumnEmpty extends PromptException {

	private static final long serialVersionUID = -2139584911736169462L;

	ExceptionDutyCodeColumnEmpty() {
		super("职务所在组织唯一编码列不能为空.");
	}
}
