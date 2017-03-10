package com.x.bbs.assemble.control.jaxrs.replyinfo;

import com.x.base.core.exception.PromptException;

class ReplyCountBySubjectException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReplyCountBySubjectException( Throwable e, String subjectId ) {
		super("根据主题ID查询主题内所有的回复数量时发生异常。Subject:" + subjectId, e );
	}
}
