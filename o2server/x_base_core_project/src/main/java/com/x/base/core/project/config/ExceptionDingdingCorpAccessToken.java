package com.x.base.core.project.config;

import com.x.base.core.project.exception.PromptException;

class ExceptionDingdingCorpAccessToken extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionDingdingCorpAccessToken(Integer code, String message) {
		super("获取钉钉  access token 失败,错误代码:{}, 错误消息:{}.", code, message);
	}
}