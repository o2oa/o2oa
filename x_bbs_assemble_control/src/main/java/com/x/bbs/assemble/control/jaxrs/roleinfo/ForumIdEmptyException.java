package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class ForumIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ForumIdEmptyException() {
		super("论坛分区id为空， 无法进行查询." );
	}
}
