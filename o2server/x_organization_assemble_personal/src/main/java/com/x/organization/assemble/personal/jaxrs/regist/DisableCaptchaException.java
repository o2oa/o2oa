package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.project.exception.PromptException;

class DisableCaptchaException extends PromptException {

	private static final long serialVersionUID = 6351023802034208595L;

	DisableCaptchaException() {
		super("系统不允许通过图片验证码注册人员.");
	}
}
