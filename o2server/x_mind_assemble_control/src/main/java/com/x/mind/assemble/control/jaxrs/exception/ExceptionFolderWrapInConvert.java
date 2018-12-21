package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionFolderWrapInConvert extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionFolderWrapInConvert( Throwable e, String jsonString ) {
		super("JsonElement转换为一个脑图目录对象时发生异常。JSON:" + jsonString, e );
	}
}
