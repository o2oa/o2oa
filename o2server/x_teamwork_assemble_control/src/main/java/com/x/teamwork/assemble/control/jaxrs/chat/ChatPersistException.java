package com.x.teamwork.assemble.control.jaxrs.chat;

import com.x.base.core.project.exception.PromptException;

class ChatPersistException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ChatPersistException( Throwable e ) {
		super("系统在保存工作交流信息时发生异常。" , e );
	}
	
	ChatPersistException( Throwable e, String message ) {
		super("系统在保存工作交流信息时发生异常。Message:" + message, e );
	}
	
	ChatPersistException( String message ) {
		super("系统在保存工作交流信息时发生异常。Message:" + message );
	}
}
