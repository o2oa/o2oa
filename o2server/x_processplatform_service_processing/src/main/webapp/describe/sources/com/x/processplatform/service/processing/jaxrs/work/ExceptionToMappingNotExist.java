package com.x.processplatform.service.processing.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionToMappingNotExist extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionToMappingNotExist(Class<?> clz) {
		super("to mapping not exist:{}.", clz.getName());
	}

}
