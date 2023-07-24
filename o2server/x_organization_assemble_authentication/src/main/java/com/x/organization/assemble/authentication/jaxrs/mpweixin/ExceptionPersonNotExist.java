package com.x.organization.assemble.authentication.jaxrs.mpweixin;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonNotExist extends PromptException {

	private static final long serialVersionUID = -1958450107076413280L;

	ExceptionPersonNotExist() {
		super("用户不存在或还未关联微信公众号");
	}
}
