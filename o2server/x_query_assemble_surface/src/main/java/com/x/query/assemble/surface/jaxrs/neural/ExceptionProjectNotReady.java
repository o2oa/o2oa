package com.x.query.assemble.surface.jaxrs.neural;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionProjectNotReady extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionProjectNotReady(String projectName) {
		super("神经网络多层感知机({})尚未准备就绪");
	}
}
