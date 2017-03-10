package com.x.okr.assemble.control.servlet.task;

import com.x.base.core.exception.PromptException;

class PersonQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	PersonQueryException( Throwable e, String flag ) {
		super("系统通过操作用户唯一标识查询用户信息时发生异常!Flag:" + flag +".", e );
	}
}
