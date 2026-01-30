package com.x.teamwork.assemble.control.jaxrs.chat;

import com.x.base.core.project.exception.PromptException;

class ChatQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ChatQueryException( Throwable e ) {
		super("系统在查询工作交流信息时发生异常。" , e );
	}
	
	ChatQueryException( Throwable e, String message ) {
		super("系统在查询工作交流信息时发生异常。Message:" + message, e );
	}
	
	ChatQueryException( String message ) {
		super("系统在查询工作交流信息时发生异常。Message:" + message );
	}
}
