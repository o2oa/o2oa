package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import com.x.base.core.exception.PromptException;

class SubjectOperationException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SubjectOperationException( Throwable e ) {
		super("对主题信息进行操作时发生异常.", e );
	}
}
