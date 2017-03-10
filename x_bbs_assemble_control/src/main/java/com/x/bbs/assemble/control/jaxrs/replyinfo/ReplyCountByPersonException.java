package com.x.bbs.assemble.control.jaxrs.replyinfo;

import com.x.base.core.exception.PromptException;

class ReplyCountByPersonException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReplyCountByPersonException( Throwable e, String name ) {
		super("根据个人查询主题内所有的回复数量时发生异常。Person:" + name, e );
	}
}
