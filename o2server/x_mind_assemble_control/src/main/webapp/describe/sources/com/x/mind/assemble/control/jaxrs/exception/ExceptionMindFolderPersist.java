package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionMindFolderPersist extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionMindFolderPersist( Throwable e, String message ) {
		super("脑图文件夹信息持久化时发生异常。MESSAGE:" + message, e );
	}
}
