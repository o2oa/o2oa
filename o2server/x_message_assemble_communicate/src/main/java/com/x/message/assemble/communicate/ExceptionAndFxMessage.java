package com.x.message.assemble.communicate;

import com.x.base.core.project.exception.PromptException;

class ExceptionAndFxMessage extends PromptException {

	private static final long serialVersionUID = -4676359299794527980L;

	ExceptionAndFxMessage(Integer retCode, String retMessage) {
		super("发送移动办公消息失败,错误代码:{},错误消息:{}.", retCode, retMessage);
	}
}
