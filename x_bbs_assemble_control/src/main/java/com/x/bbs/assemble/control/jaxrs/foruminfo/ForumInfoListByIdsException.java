package com.x.bbs.assemble.control.jaxrs.foruminfo;

import com.x.base.core.exception.PromptException;

class ForumInfoListByIdsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ForumInfoListByIdsException( Throwable e ) {
		super("系统在根据ID列表获取BBS论坛分区信息列表时发生异常！", e );
	}
}
