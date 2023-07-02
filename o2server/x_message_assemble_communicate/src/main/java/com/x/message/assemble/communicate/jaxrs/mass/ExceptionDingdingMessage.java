package com.x.message.assemble.communicate.jaxrs.mass;

import com.x.base.core.project.exception.PromptException;

class ExceptionDingdingMessage extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDingdingMessage(Integer retCode, String retMessage) {
		super("发送钉钉消息失败,错误代码:{},错误消息:{}.", retCode, retMessage);
	}
}
