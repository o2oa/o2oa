package com.x.okr.assemble.control.jaxrs.okrworkchat.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkChatSave extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkChatSave( Throwable e ) {
		super("系统保存工作交流信息记录时发生异常。", e );
	}
}
