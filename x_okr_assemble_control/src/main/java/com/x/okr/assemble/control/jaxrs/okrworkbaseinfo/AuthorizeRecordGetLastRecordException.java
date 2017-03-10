package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import com.x.base.core.exception.PromptException;

class AuthorizeRecordGetLastRecordException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AuthorizeRecordGetLastRecordException( Throwable e, String person, String id ) {
		super("系统根据工作ID以及授权相关人信息查询工作最后一次授权信息发生异常。Person: "+ person +", ID：" + id, e );
	}
}
