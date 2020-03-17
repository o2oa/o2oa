package com.x.organization.assemble.personal.jaxrs.exmail;

import com.x.base.core.project.exception.PromptException;

class ExceptionNewCount extends PromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	ExceptionNewCount(String msg) {
		super("获取新邮件数量失败:{}.", msg);
	}

}
