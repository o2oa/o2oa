package com.x.query.service.processing.jaxrs.neural;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionGenerate extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionGenerate(String model) {
		super("神经网络({})项目正在生成中.", model);
	}
}
