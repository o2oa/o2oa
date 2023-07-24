package com.x.processplatform.assemble.designer.jaxrs.process;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionProcessNotExisted extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionProcessNotExisted(String flag) {
		super("指定的流程不存在:{}.", flag);
	}
}
