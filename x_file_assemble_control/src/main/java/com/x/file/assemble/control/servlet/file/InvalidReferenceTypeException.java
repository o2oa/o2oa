package com.x.file.assemble.control.servlet.file;

import com.x.base.core.exception.PromptException;

class InvalidReferenceTypeException extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	InvalidReferenceTypeException(String name) {
		super("参考类型未知: {}.", name);
	}
}
