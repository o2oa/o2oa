package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import com.x.base.core.exception.PromptException;

class AuthorizeRecordWrapOutException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AuthorizeRecordWrapOutException( Throwable e ) {
		super("将工作授权记录查询结果转换为可以输出的数据信息时发生异常。", e );
	}
}
