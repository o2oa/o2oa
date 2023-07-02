package com.x.portal.assemble.designer.jaxrs.script;

import com.x.base.core.project.exception.LanguagePromptException;

class DependSelfException extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	DependSelfException(String name, String id) {
		super("脚本: {}, id:{}, 依赖其本身.", name, id);
	}
}
