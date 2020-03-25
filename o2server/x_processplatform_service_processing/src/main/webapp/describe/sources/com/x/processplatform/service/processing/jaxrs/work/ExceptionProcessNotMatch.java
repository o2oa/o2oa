package com.x.processplatform.service.processing.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionProcessNotMatch extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionProcessNotMatch() {
		super("流程不匹配.");
	}

}
