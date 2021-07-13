package com.x.query.assemble.designer.jaxrs.output;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionRevealNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionRevealNotExist(String flag) {
		super("展现: {} 不存在.", flag);
	}
}
