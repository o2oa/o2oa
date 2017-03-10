package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import com.x.base.core.exception.PromptException;

class AttachmentListByWorkIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttachmentListByWorkIdException( Throwable e, String id ) {
		super("系统根据工作ID列表查询附件信息列表发生异常. ID：" + id, e );
	}
}
