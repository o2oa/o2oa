package com.x.processplatform.service.processing.jaxrs.event;

import com.x.base.core.project.exception.PromptException;

class ExceptionArchiveHadoopDisable extends PromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionArchiveHadoopDisable() {
		super("归档到hadoop功能禁用.");
	}
}
