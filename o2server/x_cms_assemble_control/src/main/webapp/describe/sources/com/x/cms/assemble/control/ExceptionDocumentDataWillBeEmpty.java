package com.x.cms.assemble.control;

import com.x.base.core.project.exception.PromptException;

class ExceptionDocumentDataWillBeEmpty extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionDocumentDataWillBeEmpty(String job) {
		super("data will be empty, abort data change, job:{}.", job);
	}
}
