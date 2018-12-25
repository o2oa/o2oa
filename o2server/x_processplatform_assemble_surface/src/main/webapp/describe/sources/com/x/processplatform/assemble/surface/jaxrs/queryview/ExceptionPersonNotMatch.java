package com.x.processplatform.assemble.surface.jaxrs.queryview;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonNotMatch extends PromptException {

	private static final long serialVersionUID = -4908883340253465376L;

	ExceptionPersonNotMatch(String flag) {
		super("用户不匹配:{}.", flag);
	}

}
