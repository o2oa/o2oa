package com.x.program.center.qiyeweixin;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionListOrg extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionListOrg(Integer retCode, String retMessage) {
		super("获取下级组织失败,错误代码:{},错误消息:{}.", retCode, retMessage);
	}
}
