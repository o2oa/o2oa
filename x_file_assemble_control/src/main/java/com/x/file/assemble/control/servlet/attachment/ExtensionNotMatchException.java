package com.x.file.assemble.control.servlet.attachment;

import com.x.base.core.exception.PromptException;

class ExtensionNotMatchException extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExtensionNotMatchException(String name) {
		super("文件: {} 的扩展名不匹配.", name);
	}
}
