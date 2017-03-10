package com.x.bbs.assemble.control.jaxrs.foruminfo;

import com.x.base.core.exception.PromptException;

class ForumInfoDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ForumInfoDeleteException( Throwable e, String id ) {
		super("根据ID删除BBS论坛分区信息时发生异常.ID:" + id, e );
	}
}
