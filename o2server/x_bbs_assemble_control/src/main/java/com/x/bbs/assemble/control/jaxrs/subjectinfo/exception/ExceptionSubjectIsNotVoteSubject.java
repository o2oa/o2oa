package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSubjectIsNotVoteSubject extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSubjectIsNotVoteSubject( String id ) {
		super( "主题并不是投票主题，无法进行投票操作。ID:" + id );
	}
}
