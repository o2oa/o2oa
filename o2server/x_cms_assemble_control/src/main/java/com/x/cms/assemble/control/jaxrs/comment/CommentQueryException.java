package com.x.cms.assemble.control.jaxrs.comment;

import com.x.base.core.project.exception.PromptException;

class CommentQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CommentQueryException( Throwable e ) {
		super("系统在查询评论信息时发生异常。" , e );
	}
	
	CommentQueryException( Throwable e, String message ) {
		super("系统在查询评论信息时发生异常。Message:" + message, e );
	}
}
