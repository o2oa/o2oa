package com.x.bbs.assemble.control.jaxrs.replyinfo;

import com.x.base.core.exception.PromptException;

class SubjectLockedException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SubjectLockedException( String id ) {
		super("主题信息已被锁定，不允许进行回复.ID:" + id );
	}
}
