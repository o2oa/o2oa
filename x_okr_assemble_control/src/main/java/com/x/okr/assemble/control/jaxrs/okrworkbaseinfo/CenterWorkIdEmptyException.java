package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import com.x.base.core.exception.PromptException;

class CenterWorkIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CenterWorkIdEmptyException() {
		super("中心工作id为空，无法进行保存或者查询。" );
	}
}
