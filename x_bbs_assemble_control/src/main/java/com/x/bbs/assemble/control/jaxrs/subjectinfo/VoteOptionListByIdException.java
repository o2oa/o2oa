package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import com.x.base.core.exception.PromptException;

class VoteOptionListByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	VoteOptionListByIdException( Throwable e, String id ) {
		super("系统在根据ID查询主题的投票选项时发生异常.ID:" + id, e );
	}
}
