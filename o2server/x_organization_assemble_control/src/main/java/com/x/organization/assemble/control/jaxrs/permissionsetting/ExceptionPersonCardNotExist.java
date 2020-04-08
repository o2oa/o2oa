package com.x.organization.assemble.control.jaxrs.permissionsetting;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonCardNotExist extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionPersonCardNotExist(String flag) {
		super("通讯录设置配置, 不存在.", flag);
	}
}
