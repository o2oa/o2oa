package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception;

import com.x.base.core.exception.PromptException;

public class CenterWorkDeployException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public CenterWorkDeployException( Throwable e, String id ) {
		super("中心工作部署操作过程中发生异常。ID:" + id, e );
	}
}
