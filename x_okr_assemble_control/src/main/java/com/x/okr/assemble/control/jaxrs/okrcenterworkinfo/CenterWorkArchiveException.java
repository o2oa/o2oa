package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import com.x.base.core.exception.PromptException;

class CenterWorkArchiveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CenterWorkArchiveException( Throwable e, String id ) {
		super("中心工作归档操作过程中发生异常。ID:" + id, e );
	}
}
