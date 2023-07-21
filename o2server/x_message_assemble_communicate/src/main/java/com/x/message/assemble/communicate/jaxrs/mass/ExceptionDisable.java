package com.x.message.assemble.communicate.jaxrs.mass;

import com.x.base.core.project.exception.PromptException;

class ExceptionDisable extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDisable() {
		super("华为WeLink, 企业微信, 钉钉, 政务钉钉都没有启用.");
	}
}
