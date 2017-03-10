package com.x.bbs.assemble.control.jaxrs.replyinfo;

import com.x.base.core.exception.PromptException;

class ReplyDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReplyDeleteException( Throwable e, String id ) {
		super("根据指定ID删除回复信息时发生异常.ID:" + id, e );
	}
}
