package com.x.base.core.project.config;

import com.x.base.core.project.exception.PromptException;

class ExceptionExmailNewRemindAccessToken extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionExmailNewRemindAccessToken(Integer code, String message) {
		super("获取腾讯企业邮新邮件提醒accessToken失败,错误代码:{}, 错误消息:{}.", code, message);
	}
}