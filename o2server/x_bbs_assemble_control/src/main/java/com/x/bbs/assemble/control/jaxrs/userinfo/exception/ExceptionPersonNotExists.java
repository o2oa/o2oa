package com.x.bbs.assemble.control.jaxrs.userinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionPersonNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionPersonNotExists( String name ) {
		super("论坛分区主题分类typeCategory不合法， 无法进行查询或者保存.Name:" + name );
	}
}
