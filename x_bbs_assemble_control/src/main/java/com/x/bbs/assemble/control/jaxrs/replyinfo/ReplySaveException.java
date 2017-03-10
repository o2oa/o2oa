package com.x.bbs.assemble.control.jaxrs.replyinfo;

import com.x.base.core.exception.PromptException;

class ReplySaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReplySaveException( Throwable e ) {
		super("系统在保存回复信息时发生异常。", e );
	}
}
