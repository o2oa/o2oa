package com.x.program.center.schedule;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionZhengwuDingdingRegistCallback extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionZhengwuDingdingRegistCallback(Integer code, String message) {
		super("注册政务钉钉回调地址失败,错误代码:{}, 错误消息:{}.", code, message);
	}
}