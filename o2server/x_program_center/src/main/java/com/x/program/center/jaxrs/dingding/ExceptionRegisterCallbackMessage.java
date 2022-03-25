package com.x.program.center.jaxrs.dingding;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionRegisterCallbackMessage extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionRegisterCallbackMessage(Integer retCode, String retMessage) {
		super("发送钉钉消息失败,错误代码:{},错误消息:{}.", retCode, retMessage);
	}
}
