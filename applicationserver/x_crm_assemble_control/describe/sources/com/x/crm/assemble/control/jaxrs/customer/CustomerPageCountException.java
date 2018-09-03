package com.x.crm.assemble.control.jaxrs.customer;

import com.x.base.core.project.exception.PromptException;

public class CustomerPageCountException extends PromptException {

	private static final long serialVersionUID = -8178128931047949880L;
	public CustomerPageCountException() {
		super("rest请求路径中的page从1开始,count 需要大于1");
	}

}
