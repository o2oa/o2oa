package com.x.organization.assemble.authentication.jaxrs.qiyeweixin;

import com.x.base.core.project.exception.PromptException;

class ExceptionQywexinNotConfigured extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionQywexinNotConfigured() {
		super("没有配置企业微信登录.");
	}
}
