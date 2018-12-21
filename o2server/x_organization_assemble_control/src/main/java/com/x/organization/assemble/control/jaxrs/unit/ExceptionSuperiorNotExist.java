package com.x.organization.assemble.control.jaxrs.unit;

import com.x.base.core.project.exception.PromptException;

class ExceptionSuperiorNotExist extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionSuperiorNotExist(String flag, String superiorFlag) {
		super("组织:{}, 的上级组织: {}, 不存在.", flag, superiorFlag);
	}
}
