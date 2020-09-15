package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidIpAddress extends PromptException {

	private static final long serialVersionUID = -4915257511363100070L;

	ExceptionInvalidIpAddress(String ip) {
		super("客户端IP限制，当前IP：{}.", ip);
	}
}
