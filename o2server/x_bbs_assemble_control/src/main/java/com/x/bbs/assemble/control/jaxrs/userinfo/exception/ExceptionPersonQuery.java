package com.x.bbs.assemble.control.jaxrs.userinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionPersonQuery extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionPersonQuery( Throwable e, String name ) {
		super("系统根据人员唯一标识查询人员信息时发生异常.Name:" + name,e );
	}
}
