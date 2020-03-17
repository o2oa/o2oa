package com.x.organization.assemble.authentication.jaxrs.dingding;

import com.x.base.core.project.exception.PromptException;

class ExceptionDingding extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDingding(Integer errcode, String errmsg) {
		super("钉钉返回错误: errcode: {}, errmsg:{}.", errcode, errmsg);
	}
}
