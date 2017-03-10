package com.x.bbs.assemble.control.jaxrs.replyinfo;

import com.x.base.core.exception.PromptException;

class ReplyQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReplyQueryByIdException( Throwable e, String id ) {
		super("根据指定ID查询回复信息时发生异常.ID:" + id, e );
	}
}
