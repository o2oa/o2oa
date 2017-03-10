package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import com.x.base.core.exception.PromptException;

class SubjectSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SubjectSaveException( Throwable e ) {
		super("保存主题信息时发生异常.", e );
	}
}
