package com.x.processplatform.assemble.designer.jaxrs.script;

import com.x.base.core.exception.PromptException;

class ScriptNotExistedException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ScriptNotExistedException(String flag) {
		super("script: {} not existed.", flag);
	}
}
