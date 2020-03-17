package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.project.exception.PromptException;

public class ExceptionPersonNotExisted extends PromptException {

	private static final long serialVersionUID = -6124481323896411121L;

	public ExceptionPersonNotExisted(String name) {
		super("用户不存在:" + name + ".");
	}
}
