package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptySplitValue extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionEmptySplitValue(String id) {
		super("工作: {},拆分值不能为空.", id);
	}
}
