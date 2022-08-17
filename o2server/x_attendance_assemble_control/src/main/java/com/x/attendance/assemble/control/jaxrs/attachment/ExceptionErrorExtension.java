package com.x.attendance.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;

class ExceptionErrorExtension extends PromptException {

	private static final long serialVersionUID = -5943995811411212155L;

	public ExceptionErrorExtension(String name) {
		super("错误的文件类型: {}.", name);
	}
}
