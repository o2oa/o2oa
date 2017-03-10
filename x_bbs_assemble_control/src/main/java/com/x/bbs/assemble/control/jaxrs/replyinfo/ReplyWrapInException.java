package com.x.bbs.assemble.control.jaxrs.replyinfo;

import com.x.base.core.exception.PromptException;

class ReplyWrapInException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReplyWrapInException( Throwable e ) {
		super("将用户传入的信息转换为一个回复信息对象时发生异常。", e );
	}
}
