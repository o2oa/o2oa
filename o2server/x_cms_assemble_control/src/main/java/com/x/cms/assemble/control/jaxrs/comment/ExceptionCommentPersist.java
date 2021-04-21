package com.x.cms.assemble.control.jaxrs.comment;

import com.x.base.core.project.exception.PromptException;

class ExceptionCommentPersist extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCommentPersist( Throwable e, String message ) {
		super("系统在保存评论信息时发生异常。Message:" + message, e );
	}

}
