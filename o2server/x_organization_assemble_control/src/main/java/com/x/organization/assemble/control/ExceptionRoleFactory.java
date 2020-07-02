package com.x.organization.assemble.control;

import com.x.base.core.project.exception.PromptException;

class ExceptionRoleFactory extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionRoleFactory(Exception e) {
		super(e, "初始化角色工厂对象失败.");
	}
}
