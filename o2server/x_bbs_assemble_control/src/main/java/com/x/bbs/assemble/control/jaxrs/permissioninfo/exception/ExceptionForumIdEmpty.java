package com.x.bbs.assemble.control.jaxrs.permissioninfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionForumIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionForumIdEmpty() {
		super("论坛分区ID为空， 无法进行查询." );
	}
}
