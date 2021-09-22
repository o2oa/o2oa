package com.x.jpush.assemble.control.jaxrs.message;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSendMessageAPNSFileNotExist extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSendMessageAPNSFileNotExist() {
		super("苹果推送证书文件不存在，无法进行发送消息.");
	}
}
