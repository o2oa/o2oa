package com.x.okr.assemble.control.jaxrs.okrworkchat;

import com.x.base.core.exception.PromptException;

class WorkChatSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkChatSaveException( Throwable e ) {
		super("系统保存工作交流信息记录时发生异常。", e );
	}
}
