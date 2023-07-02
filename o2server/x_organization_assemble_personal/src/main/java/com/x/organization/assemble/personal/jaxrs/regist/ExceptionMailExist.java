package com.x.organization.assemble.personal.jaxrs.regist;

import java.util.Objects;

import com.x.base.core.project.exception.PromptException;

class ExceptionMailExist extends PromptException {

	private static final long serialVersionUID = -2489020682959861835L;

	ExceptionMailExist(String mail) {
		super("邮件:" + Objects.toString(mail) + "已注册.");
	}
}
