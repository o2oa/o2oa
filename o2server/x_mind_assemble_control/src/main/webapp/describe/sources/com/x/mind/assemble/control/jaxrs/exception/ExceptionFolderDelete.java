package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionFolderDelete extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionFolderDelete( String message ) {
		super("文件夹信息删除时发生异常！MESSAGE：" + message );
	}
	
	public ExceptionFolderDelete( Throwable e, String message ) {
		super("文件夹信息删除时发生异常！MESSAGE：" + message, e );
	}
}
