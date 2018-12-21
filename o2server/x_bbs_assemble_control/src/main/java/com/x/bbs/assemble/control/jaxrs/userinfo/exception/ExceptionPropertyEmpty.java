package com.x.bbs.assemble.control.jaxrs.userinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionPropertyEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionPropertyEmpty( String name ) {
		super("传入的数据不完整，无法保存主题信息.属性:" + name );
	}
}
