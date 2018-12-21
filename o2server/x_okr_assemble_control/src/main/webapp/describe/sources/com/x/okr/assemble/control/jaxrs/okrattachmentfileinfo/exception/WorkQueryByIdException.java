package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception;

import com.x.base.core.project.exception.PromptException;

class WorkQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkQueryByIdException( Throwable e, String id ) {
		super("查询指定ID的工作信息时发生异常。ID：" + id, e );
	}
}
