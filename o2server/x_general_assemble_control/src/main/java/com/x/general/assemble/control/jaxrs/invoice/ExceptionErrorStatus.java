package com.x.general.assemble.control.jaxrs.invoice;

import com.x.base.core.project.exception.PromptException;

class ExceptionErrorStatus extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionErrorStatus() {
		super("该票据已报销，不能删除.");
	}
}
