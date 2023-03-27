package com.x.program.center.jaxrs.script;

import com.x.base.core.project.exception.PromptException;

class DependSelfException extends PromptException {

	private static final long serialVersionUID = -1393070907328497913L;

	DependSelfException(String name, String id) {
		super("脚本: {}, id:{}, 依赖其本身.", name, id);
	}
}
