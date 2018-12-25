package com.x.message.assemble.communicate.jaxrs.connector;

import com.x.base.core.project.exception.PromptException;

class ExceptionQiyeweixinMessage extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionQiyeweixinMessage(Integer retCode, String retMessage) {
		super("发送企业微信消息失败,错误代码:{},错误消息:{}.", retCode, retMessage);
	}
}
