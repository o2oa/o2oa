package com.x.query.service.processing.jaxrs.neural;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionBundleEmpty extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionBundleEmpty(String model) {
		super("神经网络({})范围内的数据为空.", model);
	}
}
