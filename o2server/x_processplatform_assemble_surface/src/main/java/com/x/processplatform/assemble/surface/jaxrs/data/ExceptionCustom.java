package com.x.processplatform.assemble.surface.jaxrs.data;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionCustom extends LanguagePromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionCustom(String msg) {
		super(msg);
	}
}
