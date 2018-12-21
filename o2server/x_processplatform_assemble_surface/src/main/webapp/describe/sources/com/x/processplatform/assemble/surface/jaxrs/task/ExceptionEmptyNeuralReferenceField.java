package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyNeuralReferenceField extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionEmptyNeuralReferenceField() {
		super("神经网络关联失败.");
	}

}
