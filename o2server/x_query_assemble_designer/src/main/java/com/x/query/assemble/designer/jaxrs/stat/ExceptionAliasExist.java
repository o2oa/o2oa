package com.x.query.assemble.designer.jaxrs.stat;

import com.x.base.core.project.exception.PromptException;

class ExceptionAliasExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionAliasExist(String str) {
		super("别名:{},已存在.", str);
	}
}
