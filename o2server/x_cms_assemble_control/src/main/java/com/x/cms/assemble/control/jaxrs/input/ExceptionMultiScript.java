package com.x.cms.assemble.control.jaxrs.input;

import com.x.base.core.project.exception.PromptException;

class ExceptionMultiScript extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionMultiScript(String name, String alias, String id) {
		super("name: {}, alias: {}, id:{}, 找到多个脚本.", name, alias, id);
	}
}
