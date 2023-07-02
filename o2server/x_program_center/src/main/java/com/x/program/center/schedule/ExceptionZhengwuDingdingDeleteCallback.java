package com.x.program.center.schedule;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionZhengwuDingdingDeleteCallback extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionZhengwuDingdingDeleteCallback(Integer code, String message) {
		super("获取政务钉钉app access token 失败,错误代码:{}, 错误消息:{}.", code, message);
	}
}