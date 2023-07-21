package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionVoteOptionListById extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionVoteOptionListById( Throwable e, String id ) {
		super("系统在根据ID查询主题的投票选项时发生异常.ID:" + id, e );
	}
}
