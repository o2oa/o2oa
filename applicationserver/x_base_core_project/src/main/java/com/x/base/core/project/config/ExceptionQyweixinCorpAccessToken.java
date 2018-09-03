package com.x.base.core.project.config;

import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.http.EffectivePerson;

class ExceptionQyweixinCorpAccessToken extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionQyweixinCorpAccessToken(String message) {
		super("获取企业微信corp access token 失败:{}.", message);
	}
}