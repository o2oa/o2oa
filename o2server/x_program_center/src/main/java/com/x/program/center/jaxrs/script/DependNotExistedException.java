package com.x.program.center.jaxrs.script;

import com.x.base.core.project.exception.PromptException;

class DependNotExistedException extends PromptException {

	private static final long serialVersionUID = -8307485757019993917L;

	DependNotExistedException(String name, String id, String depend) {
		super("脚本: {}, id:{}, 依赖的脚本: {}, 不存在.", name, id, depend);
	}
}
