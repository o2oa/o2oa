package com.x.bbs.assemble.control.jaxrs.userinfo.exception;

import com.x.base.core.exception.PromptException;

public class PersonNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public PersonNotExistsException( String name ) {
		super("论坛分区主题分类typeCategory不合法， 无法进行查询或者保存.Name:" + name );
	}
}
