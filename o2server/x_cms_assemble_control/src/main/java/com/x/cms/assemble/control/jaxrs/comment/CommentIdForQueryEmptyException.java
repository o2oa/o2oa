package com.x.cms.assemble.control.jaxrs.comment;

import com.x.base.core.project.exception.PromptException;

class CommentIdForQueryEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CommentIdForQueryEmptyException() {
		super("查询的评论信息ID为空，无法继续查询数据。" );
	}
}
