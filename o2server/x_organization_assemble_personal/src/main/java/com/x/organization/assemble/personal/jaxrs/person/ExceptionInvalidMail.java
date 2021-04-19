package com.x.organization.assemble.personal.jaxrs.person;


import com.x.base.core.project.exception.LanguagePromptException;

 class ExceptionInvalidMail extends LanguagePromptException {

	private static final long serialVersionUID = 4622760821556680073L;

	 public static String defaultMessage = "邮件地址错误:不符合格式要求:{}.";

	 ExceptionInvalidMail(String mail) {
		super(defaultMessage, mail);
	}
}
