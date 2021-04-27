package com.x.processplatform.assemble.designer.jaxrs.script;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionScriptNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionScriptNotExist(String flag) {
		super("指定的脚本不存在:{}.", flag);
	}
}
