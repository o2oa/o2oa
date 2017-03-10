package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class ForumInfoNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ForumInfoNotExistsException( String id ) {
		super("指定ID的论坛分区不存在.ID:" + id );
	}
}
