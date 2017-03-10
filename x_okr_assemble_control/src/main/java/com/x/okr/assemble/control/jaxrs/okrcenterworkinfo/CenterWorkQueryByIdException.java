package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import com.x.base.core.exception.PromptException;

class CenterWorkQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CenterWorkQueryByIdException( Throwable e, String id ) {
		super("根据ID查询中心工作信息时发生异常。ID:" + id, e );
	}
}
