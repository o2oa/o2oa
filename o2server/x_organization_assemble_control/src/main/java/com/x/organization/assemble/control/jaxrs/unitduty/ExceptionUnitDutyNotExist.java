package com.x.organization.assemble.control.jaxrs.unitduty;

import com.x.base.core.project.exception.PromptException;

class ExceptionUnitDutyNotExist extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionUnitDutyNotExist(String flag) {
		super("组织职务:{}, 不存在.", flag);
	}
}
