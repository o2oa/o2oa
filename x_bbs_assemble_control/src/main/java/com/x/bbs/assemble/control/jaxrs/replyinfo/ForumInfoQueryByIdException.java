package com.x.bbs.assemble.control.jaxrs.replyinfo;

import com.x.base.core.exception.PromptException;

class ForumInfoQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ForumInfoQueryByIdException( Throwable e, String id ) {
		super("系统在根据ID获取BBS论坛分区信息时发生异常！ID:" + id, e );
	}
}
