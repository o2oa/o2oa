package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.PromptException;

 class ExceptionMailDuplicate extends PromptException {

	private static final long serialVersionUID = 4433998001143598936L;

	 ExceptionMailDuplicate(String mail, String fieldName) {
		super("邮件地址错误:" + mail + ", " + fieldName + "已有值重复.");
	}
}
