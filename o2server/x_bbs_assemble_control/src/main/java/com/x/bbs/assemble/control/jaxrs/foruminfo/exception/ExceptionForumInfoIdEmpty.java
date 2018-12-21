package com.x.bbs.assemble.control.jaxrs.foruminfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionForumInfoIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionForumInfoIdEmpty() {
		super("id为空， 无法进行查询." );
	}
}
