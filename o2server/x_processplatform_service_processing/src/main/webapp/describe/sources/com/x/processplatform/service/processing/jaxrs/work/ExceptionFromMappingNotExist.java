package com.x.processplatform.service.processing.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionFromMappingNotExist extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionFromMappingNotExist(String storage) {
		super("form mapping not exist:{}.", storage);
	}

}
