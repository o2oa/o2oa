package com.x.pan.assemble.control.jaxrs.attachment3;

import com.x.base.core.project.exception.PromptException;

class ExceptionErrorExtension extends PromptException {

	private static final long serialVersionUID = 362853139952092629L;

	ExceptionErrorExtension(String name) {
		super("不支持的扩展名文件: {}.", name);
	}

	ExceptionErrorExtension(String message, String name) {
		super(message, name);
	}

}
