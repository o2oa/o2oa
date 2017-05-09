package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.exception.PromptException;

public class SubjectOperationException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public SubjectOperationException( Throwable e ) {
		super("对主题信息进行操作时发生异常.", e );
	}
}
