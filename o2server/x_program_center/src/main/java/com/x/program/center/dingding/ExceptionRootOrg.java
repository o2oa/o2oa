package com.x.program.center.dingding;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionRootOrg extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionRootOrg(Integer retCode, String retMessage) {
		super("钉钉获取根组织失败,错误代码:{},错误消息:{}.", retCode, retMessage);
	}
}
