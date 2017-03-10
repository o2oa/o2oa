package com.x.bbs.assemble.control.jaxrs.foruminfo;

import com.x.base.core.exception.PromptException;

class ForumInfoSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ForumInfoSaveException( Throwable e ) {
		super("系统在保存BBS论坛分区信息时发生异常.", e );
	}
}
