package com.x.jpush.assemble.control.jaxrs.message;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSendMessageAPNSPasswordEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSendMessageAPNSPasswordEmpty() {
		super("苹果推送证书密码不存在，无法进行发送消息.");
	}
}
