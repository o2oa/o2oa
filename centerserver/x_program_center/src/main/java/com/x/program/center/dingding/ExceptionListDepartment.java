package com.x.program.center.dingding;

import com.x.base.core.project.exception.PromptException;

class ExceptionListDepartment extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionListDepartment(Long errcode, String errmsg) {
		super("钉钉请求错误,错误代码:{},错误消息:{}.", errcode, errmsg);
	}
}
