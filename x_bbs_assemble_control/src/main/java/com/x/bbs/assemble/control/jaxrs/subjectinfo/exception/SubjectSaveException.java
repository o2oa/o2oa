package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.exception.PromptException;

public class SubjectSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public SubjectSaveException( Throwable e ) {
		super("保存主题信息时发生异常.", e );
	}
}
