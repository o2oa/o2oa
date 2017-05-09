package com.x.bbs.assemble.control.jaxrs.replyinfo.exception;

import com.x.base.core.exception.PromptException;

public class PageEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public PageEmptyException() {
		super("需要查询的页码page为空， 无法进行查询." );
	}
}
