package com.x.program.center.jaxrs.collect;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidMobile extends LanguagePromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionInvalidMobile(String mobile) {
		super("手机号 {} 错误,不能为空,且需要符合手机号码格式.", mobile);
	}
}
