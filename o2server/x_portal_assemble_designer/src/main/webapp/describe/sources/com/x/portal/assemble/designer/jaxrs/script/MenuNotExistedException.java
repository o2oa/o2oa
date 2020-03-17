package com.x.portal.assemble.designer.jaxrs.script;

import com.x.base.core.project.exception.PromptException;

class ScriptNotExistedException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ScriptNotExistedException(String id) {
		super("script: {} not existed.", id);
	}
}
