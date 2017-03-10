package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import com.x.base.core.exception.PromptException;

class SubjectIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SubjectIdEmptyException() {
		super("主题ID为空， 无法进行查询." );
	}
}
