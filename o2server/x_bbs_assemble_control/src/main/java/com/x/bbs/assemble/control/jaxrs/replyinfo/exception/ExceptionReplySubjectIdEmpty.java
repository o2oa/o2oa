package com.x.bbs.assemble.control.jaxrs.replyinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionReplySubjectIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionReplySubjectIdEmpty() {
		super("主题ID为空，无法继续查询操作。" );
	}
}
