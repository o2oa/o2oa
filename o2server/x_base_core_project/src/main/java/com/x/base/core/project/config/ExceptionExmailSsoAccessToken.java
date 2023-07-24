package com.x.base.core.project.config;

import com.x.base.core.project.exception.PromptException;

class ExceptionExmailSsoAccessToken extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionExmailSsoAccessToken(Integer code, String message) {
		super("获取腾讯企业邮单点登录accessToken失败,错误代码:{}, 错误消息:{}.", code, message);
	}
}