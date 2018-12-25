package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionVoteOptionEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionVoteOptionEmpty( ) {
		super("投票贴没有提供投票选项信息，无法发表投票信息。");
	}
}
