package com.x.organization.assemble.authentication.jaxrs.welink;

import com.x.base.core.project.exception.PromptException;

class ExceptionWeLink extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionWeLink(String errcode, String errmsg) {
		super("WeLink返回错误: errcode: {}, errmsg:{}.", errcode, errmsg);
	}
}
