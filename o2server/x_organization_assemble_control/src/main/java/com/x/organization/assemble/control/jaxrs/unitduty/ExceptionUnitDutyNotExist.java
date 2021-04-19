package com.x.organization.assemble.control.jaxrs.unitduty;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionUnitDutyNotExist extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionUnitDutyNotExist(String flag) {
		super("组织职务:{}, 不存在.", flag);
	}
}
