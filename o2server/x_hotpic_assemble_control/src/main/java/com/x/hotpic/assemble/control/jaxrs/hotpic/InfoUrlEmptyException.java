package com.x.hotpic.assemble.control.jaxrs.hotpic;

import com.x.base.core.project.exception.PromptException;

class InfoUrlEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InfoUrlEmptyException() {
		super("信息访问地址为空，无法继续查询或者保存数据。" );
	}
}
