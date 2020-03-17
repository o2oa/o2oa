package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.project.exception.PromptException;

class DisableCollectException extends PromptException {

	private static final long serialVersionUID = 6351023802034208595L;

	DisableCollectException() {
		super("系统没有启用节点连接,无法发送短信.");
	}
}
