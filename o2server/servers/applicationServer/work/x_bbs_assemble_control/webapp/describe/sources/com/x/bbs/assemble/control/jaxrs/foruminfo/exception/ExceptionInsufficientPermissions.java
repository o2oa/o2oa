package com.x.bbs.assemble.control.jaxrs.foruminfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionInsufficientPermissions extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionInsufficientPermissions( String name, String role ) {
		super("操作权限不足。Name:" + name + ", Role:" + role );
	}
}
