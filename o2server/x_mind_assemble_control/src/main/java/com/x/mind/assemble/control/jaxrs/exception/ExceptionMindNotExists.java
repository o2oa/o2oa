package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionMindNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionMindNotExists( String id ) {
		super("指定ID的脑图信息对象不存在。ID:" + id );
	}
}
