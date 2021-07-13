package com.x.query.assemble.designer.jaxrs.output;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionFlagNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionFlagNotExist(String flag) {
		super("下载标识: {} 不存在.", flag);
	}
}
