package com.x.bbs.assemble.control.jaxrs.userinfo;

import com.x.base.core.exception.PromptException;

class PersonQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	PersonQueryException( Throwable e, String name ) {
		super("系统根据人员唯一标识查询人员信息时发生异常.Name:" + name,e );
	}
}
