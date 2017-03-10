package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.exception.PromptException;

class DisableCollectException extends PromptException {

	private static final long serialVersionUID = 6351023802034208595L;

	DisableCollectException() {
		super("系统没有启用节点连接.");
	}
}
