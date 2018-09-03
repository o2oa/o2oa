package com.x.crm.assemble.control.jaxrs.customer;

import com.x.base.core.project.exception.PromptException;

public class CustomernameNullException extends PromptException {
	private static final long serialVersionUID = -3885997486474873786L;

	
	CustomernameNullException() {
		super("客户名称为空，创建失败.");
	}

}
