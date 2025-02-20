package com.x.general.assemble.control.jaxrs.invoice;

import com.x.base.core.project.exception.PromptException;

class ExceptionErrorExtract extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionErrorExtract(String msg) {
		super("电子发票解析异常: {}.", msg);
	}
}
