package com.x.processplatform.assemble.surface.jaxrs.handover;

import com.x.base.core.project.exception.PromptException;

class ExceptionHasProcessed extends PromptException {

	private static final long serialVersionUID = -6309924494820409666L;

	ExceptionHasProcessed() {
		super("该任务已完成.");
	}
}
