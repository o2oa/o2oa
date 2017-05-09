package com.x.organization.assemble.control.alpha.jaxrs.person;

import com.x.base.core.exception.PromptException;

 class WeixinDuplicateException extends PromptException {

	private static final long serialVersionUID = 4433998001143598936L;

	 WeixinDuplicateException(String weixin, String fieldName) {
		super("微信号错误:" + weixin + ", " + fieldName + "已有值重复.");
	}
}
