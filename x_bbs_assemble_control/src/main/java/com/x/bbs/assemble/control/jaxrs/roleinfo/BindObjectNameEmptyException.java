package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class BindObjectNameEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	BindObjectNameEmptyException() {
		super("绑定对象为空， 无法进行查询." );
	}
}
