package com.x.bbs.assemble.control.jaxrs.replyinfo;

import com.x.base.core.exception.PromptException;

class ReplyListByPersonException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReplyListByPersonException( Throwable e, String name ) {
		super("根据个人查询主题内所有的回复列表时发生异常。Person:" + name, e );
	}
}
