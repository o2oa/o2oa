package com.x.okr.assemble.control.jaxrs.okrconfigsercretary;

import com.x.base.core.exception.PromptException;

class SercretaryConfigQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SercretaryConfigQueryByIdException( Throwable e, String id ) {
		super("系统根据ID查询领导秘书配置信息时发生异常。ID:"+id, e);
	}
}
