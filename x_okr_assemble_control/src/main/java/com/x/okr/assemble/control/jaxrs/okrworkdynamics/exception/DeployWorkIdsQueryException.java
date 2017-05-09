package com.x.okr.assemble.control.jaxrs.okrworkdynamics.exception;

import com.x.base.core.exception.PromptException;

public class DeployWorkIdsQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public DeployWorkIdsQueryException( Throwable e, String identity ) {
		super("系统获取用户部署的中心工作时发生异常. Person:" + identity, e );
	}
}
