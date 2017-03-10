package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.exception.PromptException;

 class MailDuplicateException extends PromptException {

	private static final long serialVersionUID = 4433998001143598936L;

	 MailDuplicateException(String mail, String fieldName) {
		super("邮件地址错误:" + mail + ", " + fieldName + "已有值重复.");
	}
}
