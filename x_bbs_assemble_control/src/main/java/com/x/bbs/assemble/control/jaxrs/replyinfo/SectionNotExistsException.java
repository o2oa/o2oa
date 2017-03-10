package com.x.bbs.assemble.control.jaxrs.replyinfo;

import com.x.base.core.exception.PromptException;

class SectionNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SectionNotExistsException( String id ) {
		super("指定ID的版块信息不存在.ID:" + id );
	}
}
