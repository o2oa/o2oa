package com.x.query.service.processing.jaxrs.neural;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionModelNotReady extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionModelNotReady(String model) {
		super("神经网络({})尚未准备就绪.", model);
	}
}
