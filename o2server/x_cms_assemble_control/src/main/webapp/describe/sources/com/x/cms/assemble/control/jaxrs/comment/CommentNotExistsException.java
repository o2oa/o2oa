package com.x.cms.assemble.control.jaxrs.comment;

import com.x.base.core.project.exception.PromptException;

class CommentNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CommentNotExistsException( String id ) {
		super("指定ID的评论信息不存在。ID:" + id );
	}
}
