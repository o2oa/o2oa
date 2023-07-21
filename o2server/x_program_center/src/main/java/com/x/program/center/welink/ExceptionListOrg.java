package com.x.program.center.welink;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionListOrg extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionListOrg(String retCode, String retMessage) {
		super("WeLink获取下级组织失败,错误代码:{},错误消息:{}.", retCode, retMessage);
	}
}
