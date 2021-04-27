package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptySplitValueAfterTrim extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionEmptySplitValueAfterTrim(String id) {
		super("工作: {},过滤后的拆分值为空.", id);
	}
}
