package com.x.okr.assemble.control.jaxrs.okrworkchat;

import com.x.base.core.exception.PromptException;

class WorkChatQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkChatQueryByIdException( Throwable e, String id ) {
		super("系统根据ID查询指定的工作交流信息记录时发生异常。ID:" + id, e );
	}
}
