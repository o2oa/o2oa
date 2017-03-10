package com.x.bbs.assemble.control.jaxrs.replyinfo;

import com.x.base.core.exception.PromptException;

class ReplyListBySubjectException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReplyListBySubjectException( Throwable e, String subjectId ) {
		super("根据主题ID查询主题内所有的回复列表时发生异常。Subject:" + subjectId, e );
	}
}
