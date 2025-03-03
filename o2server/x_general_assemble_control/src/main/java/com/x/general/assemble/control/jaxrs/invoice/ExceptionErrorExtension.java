package com.x.general.assemble.control.jaxrs.invoice;

import com.x.base.core.project.exception.PromptException;

class ExceptionErrorExtension extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionErrorExtension(String name) {
		super("仅支持pdf文件: {}.", name);
	}
}
