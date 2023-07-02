package com.x.portal.assemble.designer.jaxrs.script;

import com.x.base.core.project.exception.LanguagePromptException;

class ScriptNotExistedException extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ScriptNotExistedException(String id) {
		super("指定的脚本不存在:{}.", id);
	}
}
