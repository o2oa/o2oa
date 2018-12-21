package com.x.query.assemble.designer.jaxrs.neural.mlp;

import com.x.base.core.project.exception.PromptException;

class ExceptionLearning extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionLearning(String projectName) {
		super("神经网络多层感知机({})项目正在学习中.", projectName);
	}
}
