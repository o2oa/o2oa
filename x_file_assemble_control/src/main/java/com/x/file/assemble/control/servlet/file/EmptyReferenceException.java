package com.x.file.assemble.control.servlet.file;

import com.x.base.core.exception.PromptException;

class EmptyReferenceException extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	EmptyReferenceException(String name) {
		super("参考值不能为空: {}.", name);
	}
}
