package com.x.jpush.assemble.control.jaxrs.message;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSendMessageDeviceEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSendMessageDeviceEmpty() {
		super("绑定设备为空，无法进行发送消息.");
	}
}
