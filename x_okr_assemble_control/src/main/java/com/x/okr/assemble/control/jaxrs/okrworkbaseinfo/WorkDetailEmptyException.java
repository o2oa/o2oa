package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import com.x.base.core.exception.PromptException;

class WorkDetailEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkDetailEmptyException() {
		super("工作描述内容为空，无法进行工作保存。" );
	}
}
