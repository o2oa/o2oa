package com.x.cms.assemble.control.jaxrs.comment;

import com.x.base.core.project.exception.PromptException;

class ExceptionCommentIdForQueryEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCommentIdForQueryEmpty() {
		super("查询的评论信息ID为空，无法继续查询数据。" );
	}
}
