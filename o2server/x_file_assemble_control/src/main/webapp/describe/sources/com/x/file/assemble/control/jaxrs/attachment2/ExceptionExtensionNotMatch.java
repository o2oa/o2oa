package com.x.file.assemble.control.jaxrs.attachment2;

import com.x.base.core.project.exception.PromptException;

class ExceptionExtensionNotMatch extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionExtensionNotMatch(String name, String extension) {
		super("文件: {} 的扩展名不匹配,期望的扩展名: {}.", name);
	}
}
