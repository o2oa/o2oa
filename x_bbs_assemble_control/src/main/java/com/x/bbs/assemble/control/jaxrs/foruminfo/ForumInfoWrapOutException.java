package com.x.bbs.assemble.control.jaxrs.foruminfo;

import com.x.base.core.exception.PromptException;

class ForumInfoWrapOutException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ForumInfoWrapOutException( Throwable e ) {
		super("系统在转换所有BBS论坛分区信息为输出对象时发生异常.", e );
	}
}
