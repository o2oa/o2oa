package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionFolderPersist extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionFolderPersist( Throwable e ) {
		super("文件夹信息保存时发生异常！", e );
	}
}
