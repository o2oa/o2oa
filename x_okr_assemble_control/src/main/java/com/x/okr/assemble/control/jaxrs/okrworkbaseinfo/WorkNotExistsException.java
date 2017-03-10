package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import com.x.base.core.exception.PromptException;

class WorkNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkNotExistsException( String id ) {
		super("指定ID的具体工作信息记录不存在。ID：" + id );
	}
}
