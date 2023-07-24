package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionMindRecycleInfoNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionMindRecycleInfoNotExists( String id ) {
		super("指定ID的回收站脑图信息对象不存在。ID:" + id );
	}
}
