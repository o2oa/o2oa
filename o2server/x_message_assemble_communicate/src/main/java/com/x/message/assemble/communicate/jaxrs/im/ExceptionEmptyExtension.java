package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyExtension extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionEmptyExtension(String name) {
		super("不能上传文件扩展名为空的文件: {}.", name);
	}

	ExceptionEmptyExtension(String message, String name) {
		super(message, name);
	}

}
