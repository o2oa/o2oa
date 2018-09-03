package com.x.crm.assemble.control.jaxrs.customer;

import com.x.base.core.project.exception.PromptException;

public class CustomerMustUniqueException extends PromptException {

	private static final long serialVersionUID = -4707214121897828874L;

	CustomerMustUniqueException() {
		super("客户名称不能重复.");
	}
}
