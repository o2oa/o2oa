package com.x.processplatform.assemble.surface.jaxrs.draft;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDecideCreatorIdentity extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionDecideCreatorIdentity() {
		super("识别身份错误.");
	}
}
