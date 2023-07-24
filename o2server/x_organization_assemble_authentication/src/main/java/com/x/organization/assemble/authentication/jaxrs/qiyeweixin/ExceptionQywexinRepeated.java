package com.x.organization.assemble.authentication.jaxrs.qiyeweixin;

import com.x.base.core.project.exception.PromptException;

class ExceptionQywexinRepeated extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionQywexinRepeated(String userId) {
		super("查询到重复的企业微信userid. {} " ,userId);
	}
}
