package com.x.program.center.jaxrs.zhengwudingding;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionGetRegistCallback extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionGetRegistCallback(Integer retCode, String retMessage) {
		super("获取政务钉钉注册的回调地址信息失败,错误代码:{},错误消息:{}.", retCode, retMessage);
	}
}
