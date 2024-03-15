package com.x.processplatform.assemble.surface.jaxrs.handover;

import com.x.base.core.project.exception.PromptException;

class ExceptionHasProcess extends PromptException {

	private static final long serialVersionUID = -6309924494820409666L;

	ExceptionHasProcess(String operate) {
		super("该任务处于运行中或已完成,{}.", operate);
	}
}
