package com.x.organization.assemble.authentication.jaxrs.zhengwudingding;

import com.x.base.core.project.exception.PromptException;

class ExceptionGetUserIdWithDingUserId extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionGetUserIdWithDingUserId(Integer errcode, String errmsg) {
		super("用钉钉userId交换政务钉钉userId错误, 代码{}, 消息:{}.", errcode, errmsg);
	}
}
