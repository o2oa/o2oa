package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import com.x.base.core.exception.PromptException;

class CenterWorkDeployException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CenterWorkDeployException( Throwable e, String id ) {
		super("中心工作部署操作过程中发生异常。ID:" + id, e );
	}
}
