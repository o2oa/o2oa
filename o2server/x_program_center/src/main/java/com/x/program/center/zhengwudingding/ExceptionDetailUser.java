package com.x.program.center.zhengwudingding;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDetailUser extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDetailUser(Integer retCode, String retMessage) {
		super("政务钉钉获取人员信息失败,错误代码:{},错误消息:{}.", retCode, retMessage);
	}
}
