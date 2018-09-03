package com.x.crm.assemble.control.jaxrs.customer;

import com.x.base.core.project.exception.PromptException;

public class CustomerNotExistedException extends PromptException {
	private static final long serialVersionUID = -3885997486474873786L;
	
	CustomerNotExistedException(String name) {
		super("指定的用户:" + name + ", 不存在.");
	}

}
