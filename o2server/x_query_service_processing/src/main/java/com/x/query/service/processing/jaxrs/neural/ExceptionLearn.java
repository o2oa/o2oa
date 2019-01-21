package com.x.query.service.processing.jaxrs.neural;

import com.x.base.core.project.exception.PromptException;

class ExceptionLearn extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionLearn(String model) {
		super("神经网络({})正在学习中.", model);
	}
}
