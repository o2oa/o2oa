package com.x.message.assemble.communicate;

import com.x.base.core.project.exception.PromptException;

class ExceptionMQMessage extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionMQMessage(Integer retCode, String retMessage) {
		super("发送消息队列失败,错误代码:{},错误消息:{}.", retCode, retMessage);
	}
}
