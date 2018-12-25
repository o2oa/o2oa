package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionFolderMoveToFolder extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionFolderMoveToFolder( String message ) {
		super("将文件夹移动到指定文件夹时发生异常！MESSAGE：" + message );
	}
	
	public ExceptionFolderMoveToFolder( Throwable e, String message ) {
		super("将文件夹信息移动到指定文件夹时发生异常！MESSAGE：" + message, e );
	}
}
