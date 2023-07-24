package com.x.hotpic.assemble.control.jaxrs.hotpic;

import com.x.base.core.project.exception.PromptException;

class InfoIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InfoIdEmptyException() {
		super("信息Id为空，无法继续查询或者保存数据。" );
	}
}
