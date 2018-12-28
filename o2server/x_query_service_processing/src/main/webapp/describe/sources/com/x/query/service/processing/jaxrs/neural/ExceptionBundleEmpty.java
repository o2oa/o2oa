package com.x.query.service.processing.jaxrs.neural;

import com.x.base.core.project.exception.PromptException;

class ExceptionBundleEmpty extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionBundleEmpty(String projectName) {
		super("神经网络多层感知机({})范围内的数据为空.");
	}
}
