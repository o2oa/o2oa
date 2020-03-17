package com.x.cms.assemble.control.jaxrs.comment;

import com.x.base.core.project.exception.PromptException;

class CommentPersistException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CommentPersistException( Throwable e ) {
		super("系统在保存评论信息时发生异常。" , e );
	}
	
	CommentPersistException( Throwable e, String message ) {
		super("系统在保存评论信息时发生异常。Message:" + message, e );
	}
	
	CommentPersistException( String message ) {
		super("系统在保存评论信息时发生异常。Message:" + message );
	}
}
