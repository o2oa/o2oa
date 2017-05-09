package com.x.portal.assemble.surface.jaxrs.script;

import com.x.base.core.exception.PromptException;

class ScriptNotExistedException extends PromptException {

	private static final long serialVersionUID = -4908883340253465376L;

	ScriptNotExistedException(String id) {
		super("指定的脚本不存在:{}.", id);
	}

}
