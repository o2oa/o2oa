package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import com.x.base.core.exception.PromptException;

class AuthorizeRecordListByIdsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AuthorizeRecordListByIdsException( Throwable e ) {
		super("系统根据ID列表查询工作授权信息列表发生异常。", e );
	}
}
