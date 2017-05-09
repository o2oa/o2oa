package com.x.okr.assemble.control.jaxrs.okrworkchat.exception;

import com.x.base.core.exception.PromptException;

public class WorkChatSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkChatSaveException( Throwable e ) {
		super("系统保存工作交流信息记录时发生异常。", e );
	}
}
