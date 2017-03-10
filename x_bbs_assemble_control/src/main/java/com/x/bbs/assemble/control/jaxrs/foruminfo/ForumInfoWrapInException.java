package com.x.bbs.assemble.control.jaxrs.foruminfo;

import com.x.base.core.exception.PromptException;

class ForumInfoWrapInException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ForumInfoWrapInException( Throwable e ) {
		super("将用户传入的信息转换为一个论坛分区信息对象时发生异常。", e );
	}
}
