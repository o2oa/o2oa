package com.x.message.assemble.communicate.ws.collaboration;

import java.util.Objects;

import com.x.base.core.project.exception.PromptException;

 class ExceptionInvalidName extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	 ExceptionInvalidName(String name) {
		super("用户名错误,不能为空,不能使用保留字串,且不能使用特殊字符:" + Objects.toString(name) + ".");
	}
}
