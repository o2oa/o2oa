package com.x.query.assemble.designer.jaxrs.neural;

import com.x.base.core.project.exception.PromptException;

class ExceptionGenerating extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionGenerating(String projectName) {
		super("神经网络多层感知机({})项目正在生成中.", projectName);
	}
}
