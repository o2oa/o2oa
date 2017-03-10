package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import com.x.base.core.exception.PromptException;

class WorkDeployException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkDeployException( Throwable e ) {
		super("部署具体工作过程中发生异常。", e );
	}
}
