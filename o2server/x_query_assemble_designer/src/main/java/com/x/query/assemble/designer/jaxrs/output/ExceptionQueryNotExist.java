package com.x.query.assemble.designer.jaxrs.output;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionQueryNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionQueryNotExist(String flag) {
		super("站点:{}不存在.", flag);
	}
}
