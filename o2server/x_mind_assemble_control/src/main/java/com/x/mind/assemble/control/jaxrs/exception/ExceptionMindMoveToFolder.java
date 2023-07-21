package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionMindMoveToFolder extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionMindMoveToFolder( String message ) {
		super("将脑图信息移动到指定文件夹时发生异常！MESSAGE：" + message );
	}
	
	public ExceptionMindMoveToFolder( Throwable e, String message ) {
		super("将脑图信息移动到指定文件夹时发生异常！MESSAGE：" + message, e );
	}
}
