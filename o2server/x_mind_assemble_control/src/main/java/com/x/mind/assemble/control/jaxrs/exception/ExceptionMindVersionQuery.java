package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionMindVersionQuery extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionMindVersionQuery( Throwable e, String message ) {
		super("脑图版本信息查询时发生异常。MESSAGE:" + message, e );
	}
	
	public ExceptionMindVersionQuery( Throwable e, String message,  String id ) {
		super("脑图版本信息查询时发生异常。ID:" + id + ",  MESSAGE:" + message , e );
	}
}
