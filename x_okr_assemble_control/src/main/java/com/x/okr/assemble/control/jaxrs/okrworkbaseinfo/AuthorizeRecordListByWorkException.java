package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import com.x.base.core.exception.PromptException;

class AuthorizeRecordListByWorkException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AuthorizeRecordListByWorkException( Throwable e, String id ) {
		super("系统根据工作ID以及授权相关人信息查询工作最后一次授权信息发生异常。ID：" + id, e );
	}
}
