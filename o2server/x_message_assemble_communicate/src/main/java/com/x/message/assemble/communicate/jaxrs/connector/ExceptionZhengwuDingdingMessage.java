package com.x.message.assemble.communicate.jaxrs.connector;

import com.x.base.core.project.exception.PromptException;

class ExceptionZhengwuDingdingMessage extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionZhengwuDingdingMessage(Integer retCode, String retMessage) {
		super("发送政务钉钉消息失败,错误代码:{},错误消息:{}.", retCode, retMessage);
	}
}
