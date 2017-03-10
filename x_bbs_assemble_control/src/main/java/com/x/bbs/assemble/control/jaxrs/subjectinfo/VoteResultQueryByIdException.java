package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import com.x.base.core.exception.PromptException;

class VoteResultQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	VoteResultQueryByIdException( Throwable e, String id ) {
		super("根据指定ID查询主题信息投票结果时发生异常.ID:" + id, e );
	}
}
