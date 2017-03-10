package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.exception.PromptException;

public class PersonNotExistedException extends PromptException {

	private static final long serialVersionUID = -6124481323896411121L;

	public PersonNotExistedException(String name) {
		super("用户不存在:" + name + ".");
	}
}
