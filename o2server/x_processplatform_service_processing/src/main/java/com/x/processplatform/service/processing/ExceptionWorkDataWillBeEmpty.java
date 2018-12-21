package com.x.processplatform.service.processing;

import com.x.base.core.project.exception.PromptException;

class ExceptionWorkDataWillBeEmpty extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionWorkDataWillBeEmpty(String job) {
		super("data will be empty, abort data change, job:{}.", job);
	}
}
