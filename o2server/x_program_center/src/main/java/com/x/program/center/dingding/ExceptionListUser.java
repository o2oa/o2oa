package com.x.program.center.dingding;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionListUser extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionListUser(Integer retCode, String retMessage) {
		super("钉钉获取组织成员失败,错误代码:{},错误消息:{}.", retCode, retMessage);
	}
}
