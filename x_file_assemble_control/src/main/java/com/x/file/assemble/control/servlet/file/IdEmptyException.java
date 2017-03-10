package com.x.file.assemble.control.servlet.file;

import com.x.base.core.exception.PromptException;

class IdEmptyException extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	IdEmptyException() {
		super("无法获取对象标识.");
	}
}
