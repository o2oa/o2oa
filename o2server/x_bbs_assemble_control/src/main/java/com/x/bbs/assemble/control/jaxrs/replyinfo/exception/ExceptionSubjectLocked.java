package com.x.bbs.assemble.control.jaxrs.replyinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSubjectLocked extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSubjectLocked( String id ) {
		super( "主题信息已被锁定，不允许进行回复.ID:" + id );
	}
}
