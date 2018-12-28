package com.x.query.assemble.designer.jaxrs.neural;

import com.x.base.core.project.exception.PromptException;

class ExceptionEntryEmpty extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionEntryEmpty(String projectName) {
		super("神经网络多层感知机({})项目训练数据集为空.", projectName);
	}
}
