package com.x.okr.assemble.control.jaxrs.okrworkchat.exception;

import com.x.base.core.exception.PromptException;

public class WorkChatDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkChatDeleteException( Throwable e, String id ) {
		super("系统根据ID删除指定的工作交流信息记录时发生异常。ID:" + id, e );
	}
}
