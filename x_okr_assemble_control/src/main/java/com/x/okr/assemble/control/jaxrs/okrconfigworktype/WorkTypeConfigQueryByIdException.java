package com.x.okr.assemble.control.jaxrs.okrconfigworktype;

import com.x.base.core.exception.PromptException;

class WorkTypeConfigQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkTypeConfigQueryByIdException( Throwable e, String id ) {
		super("系统根据ID删除指定的工作类别配置时发生异常。ID:" + id, e);
	}
}
