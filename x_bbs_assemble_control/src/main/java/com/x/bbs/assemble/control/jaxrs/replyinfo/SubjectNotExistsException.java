package com.x.bbs.assemble.control.jaxrs.replyinfo;

import com.x.base.core.exception.PromptException;

class SubjectNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SubjectNotExistsException( String id ) {
		super("指定ID的主题信息不存在.ID:" + id );
	}
}
