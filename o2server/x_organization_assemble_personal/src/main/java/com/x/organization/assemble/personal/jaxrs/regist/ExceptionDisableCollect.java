package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDisableCollect extends LanguagePromptException {

	private static final long serialVersionUID = 6351023802034208595L;

	ExceptionDisableCollect() {
		super("短信发送失败，请联系管理员配置O2云连接.");
	}
}
