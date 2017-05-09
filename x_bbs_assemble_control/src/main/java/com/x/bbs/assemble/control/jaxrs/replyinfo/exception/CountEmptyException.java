package com.x.bbs.assemble.control.jaxrs.replyinfo.exception;

import com.x.base.core.exception.PromptException;

public class CountEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;
	
	public CountEmptyException() {
		super("每页条目数 count 为空， 无法进行查询." );
	}
	
}
