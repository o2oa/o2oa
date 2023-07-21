package com.x.processplatform.service.processing.jaxrs.data;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.PromptException;

class ExceptionParentNotExist extends PromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionParentNotExist(String[] paths) {
		super("parent not exist: {}.", StringUtils.join(paths, "."));
	}
}
