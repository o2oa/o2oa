package com.x.bbs.assemble.control.jaxrs.replyinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionReplyNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionReplyNotExists( String id ) {
		super("指定ID的回复信息不存在.ID:" + id );
	}
}
