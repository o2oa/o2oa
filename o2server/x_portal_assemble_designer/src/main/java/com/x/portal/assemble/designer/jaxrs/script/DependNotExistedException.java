package com.x.portal.assemble.designer.jaxrs.script;

import com.x.base.core.project.exception.LanguagePromptException;

class DependNotExistedException extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	DependNotExistedException(String name, String id, String depend) {
		super("脚本: {}, id:{}, 依赖的脚本: {}, 不存在.", name, id, depend);
	}
}
