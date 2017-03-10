package com.x.bbs.assemble.control.jaxrs.replyinfo;

import com.x.base.core.exception.PromptException;

class ReplyWrapOutException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReplyWrapOutException( Throwable e ) {
		super("将查询结果转换成可以输出的数据信息时发生异常。", e );
	}
}
