package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmpowerRecordProcessing extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionEmpowerRecordProcessing(String job) {
		super("授权记录失败,job: {}.", job);
	}

}
